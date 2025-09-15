package com.phantom.learningservice.service.impl;

import com.phantom.common.bean.dto.UserBaseInfoDTO;
import com.phantom.common.bean.vo.CourseInfoVo;
import com.phantom.common.bean.vo.R;
import com.phantom.common.constant.Code;
import com.phantom.learningservice.bean.dto.AssignmentSubmissionDTO;
import com.phantom.learningservice.bean.dto.EnrollmentDTO;
import com.phantom.learningservice.bean.dto.ProgressUpdateDTO;
import com.phantom.learningservice.bean.po.LearningProgressPO;
import com.phantom.learningservice.bean.po.UserAssignmentPO;
import com.phantom.learningservice.bean.po.UserCoursePO;
import com.phantom.learningservice.bean.vo.LearningProgressVO;
import com.phantom.learningservice.bean.vo.UserAssignmentVO;
import com.phantom.learningservice.bean.vo.UserCourseVO;
import com.phantom.learningservice.constant.LearningConstant;
import com.phantom.learningservice.exception.LearningServiceException;
import com.phantom.learningservice.feign.CourseClient;
import com.phantom.learningservice.feign.UserClient;
import com.phantom.learningservice.mapper.LearningProgressMapper;
import com.phantom.learningservice.mapper.UserAssignmentMapper;
import com.phantom.learningservice.mapper.UserCourseMapper;
import com.phantom.learningservice.service.LearningService;
import com.phantom.learningservice.util.LearningUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.vavr.CheckedFunction0;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {
    @Autowired
    private UserCourseMapper userCourseMapper;
    @Autowired
    private LearningProgressMapper learningProgressMapper;
    @Autowired
    private UserAssignmentMapper userAssignmentMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //服务间调用
    @Autowired
    private UserClient userClient;
    @Autowired
    private CourseClient courseClient;

    //注入Redisson客户端
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CircuitBreaker enrollCircuitBreaker;
    @Autowired
    private RateLimiter enrollRateLimiter;

    @Transactional
    @Override
    public UserCourseVO enrollUserInCourse(EnrollmentDTO enrollmentDTO){
        // 1. 限流检查
        if (!enrollRateLimiter.acquirePermission()) {
            throw new LearningServiceException("系统繁忙，请稍后重试");
        }

        // 2. 熔断保护
        CheckedFunction0<UserCourseVO> enrollmentSupplier=CircuitBreaker.
                decorateCheckedSupplier(enrollCircuitBreaker,()->{
                    return performEnrollment(enrollmentDTO);
                });
        try{
            return enrollmentSupplier.apply();
        }catch (Throwable e){
            if(e instanceof LearningServiceException){
                throw (LearningServiceException) e;
            }
            log.error("Circuit breaker detected failure in enrollment service",e);
            throw new LearningServiceException("选课服务暂时不可用，请稍后重试");
        }

    }

    private UserCourseVO performEnrollment(EnrollmentDTO enrollmentDTO){
        String localKey="enroll:lock:user:"+enrollmentDTO.getUserId()+":course:"+enrollmentDTO.getCourseId();
        RLock lock=redissonClient.getLock(localKey);
        try {
            //尝试获得锁
            boolean locked=lock.tryLock(1,5, TimeUnit.SECONDS);
            if(!locked){
                log.warn("Failed to acquire lock for userId={},courseId={}",enrollmentDTO.getUserId(),enrollmentDTO.getCourseId());
                throw new LearningServiceException("选课失败，系统繁忙，请稍后再试");
            }

            //验证用户
            R<UserBaseInfoDTO> userResponse=userClient.getUserBaseInfoById(enrollmentDTO.getUserId());
            if(userResponse.getCode()!= Code.SUCCESS){
                throw new LearningServiceException("User not found"+enrollmentDTO.getUserId());
            }
            //验证课程
            R<CourseInfoVo> courseResponse=courseClient.getCourseInfo(enrollmentDTO.getCourseId());
            if(courseResponse.getCode()!=Code.SUCCESS){
                throw new LearningServiceException("Course not found"+enrollmentDTO.getCourseId());
            }

            //检查课程容量
            if (!checkCourseCapacity(enrollmentDTO.getCourseId())) {
                throw new LearningServiceException("课程已满");
            }

            //插入记录
            UserCoursePO userCoursePO=new UserCoursePO();
            userCoursePO.setUserId(enrollmentDTO.getUserId());
            userCoursePO.setCourseId(enrollmentDTO.getCourseId());
            userCoursePO.setEnrollmentDate(new Timestamp(System.currentTimeMillis()));
            userCoursePO.setStatus("ENROLLED");
            userCourseMapper.insert(userCoursePO);

            //发送消息  (这段代码是生产者)
            Map<String,Object> message=new HashMap<>();
            message.put("userId",enrollmentDTO.getUserId());
            message.put("courseId",enrollmentDTO.getCourseId());
            message.put("timestamp",System.currentTimeMillis());
            rabbitTemplate.convertAndSend(LearningConstant.LEARNING_EXCHANGE,LearningConstant.ENROLLMENT_ROUTING_KEY,message);

            return LearningUtil.convertToVO(userCoursePO);
        }  catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LearningServiceException("Enrollment interrupted: "+e.getMessage());
        } catch (Exception e) {
            log.error("Enrollment failed for userId={}.courseId={}",enrollmentDTO.getUserId(),enrollmentDTO.getCourseId());
            throw new LearningServiceException("Enrollment failed: "+e.getMessage());
        }
        finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
                log.debug("Lock released for userId={},courseId={}",enrollmentDTO.getUserId(),enrollmentDTO.getCourseId());
            }
        }
    }

    private boolean checkCourseCapacity(Integer courseId) {
        // TODO: 实现课程容量检查逻辑
        return true;
    }

    @Override
    public LearningProgressVO updateLearningProgress(ProgressUpdateDTO progressUpdateDTO){
        LearningProgressPO learningProgress=learningProgressMapper.findByUserIdAndCourseId(
                progressUpdateDTO.getUserId(),progressUpdateDTO.getCourseId()
        );

        if(learningProgress==null){
            learningProgress=new LearningProgressPO();
            learningProgress.setUserId(progressUpdateDTO.getUserId());
            learningProgress.setCourseId(progressUpdateDTO.getCourseId());

        }
        learningProgress.setProgress(progressUpdateDTO.getProgress());
        learningProgress.setLastUpdated(new Timestamp(new Date().getTime()));

        if(learningProgress.getId()==null){
            learningProgressMapper.insert(learningProgress);
        }else {
            learningProgressMapper.update(learningProgress);
        }
        return LearningUtil.convertToVO(learningProgress);
    }
    @Override
    public UserAssignmentVO submitAssignment(AssignmentSubmissionDTO submissionDTO){
        UserAssignmentPO userAssignment=new UserAssignmentPO();
        userAssignment.setUserId(submissionDTO.getUserId());
        userAssignment.setAssignmentId(submissionDTO.getAssignmentId());
        userAssignment.setSubmissionDate(new Timestamp(new Date().getTime()));

        userAssignmentMapper.insert(userAssignment);
        return LearningUtil.convertToVO(userAssignment);
    }
}
