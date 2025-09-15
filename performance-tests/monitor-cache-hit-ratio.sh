#!/bin/bash

# 设置API地址
CACHE_STATS_API="http://localhost:1238/api/courses/monitoring/cache/stats"
REDIS_INFO_API="http://localhost:1238/api/courses/monitoring/redis/info"

# 创建结果目录
RESULTS_DIR="./logs/cache_stats"
mkdir -p $RESULTS_DIR

# 获取当前时间戳
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# 获取缓存统计数据
echo "正在获取缓存命中率数据..."
cache_stats=$(curl -s $CACHE_STATS_API)

# 检查响应
if [[ $cache_stats == *"\"code\":200"* ]]; then
    # 提取数据
    course_info=$(echo $cache_stats | grep -o '"courseInfo":[0-9\.]*' | cut -d':' -f2)
    courses_by_category=$(echo $cache_stats | grep -o '"coursesByCategory":[0-9\.]*' | cut -d':' -f2)
    latest_courses=$(echo $cache_stats | grep -o '"latestCourses":[0-9\.]*' | cut -d':' -f2)
    
    echo "缓存命中率统计："
    echo "课程详情缓存(courseInfo)：$course_info%"
    echo "分类课程缓存(coursesByCategory)：$courses_by_category%"
    echo "最新课程缓存(latestCourses)：$latest_courses%"
    
    # 保存到文件
    echo "$TIMESTAMP, $course_info, $courses_by_category, $latest_courses" >> $RESULTS_DIR/cache_hit_ratio.csv
else
    echo "获取缓存统计数据失败，API响应: $cache_stats"
fi

# 获取Redis服务器命中率数据
echo -e "\n正在获取Redis服务器命中率数据..."
redis_info=$(curl -s $REDIS_INFO_API)

# 检查响应
if [[ $redis_info == *"\"code\":200"* ]]; then
    # 提取数据
    keyspace_hits=$(echo $redis_info | grep -o '"keyspace_hits":"[^"]*"' | cut -d'"' -f4)
    keyspace_misses=$(echo $redis_info | grep -o '"keyspace_misses":"[^"]*"' | cut -d'"' -f4)
    hit_ratio=$(echo $redis_info | grep -o '"hit_ratio":"[^"]*"' | cut -d'"' -f4)
    
    echo "Redis服务器命中统计："
    echo "命中次数：$keyspace_hits"
    echo "未命中次数：$keyspace_misses"
    echo "命中率：$hit_ratio"
    
    # 保存到文件
    echo "$TIMESTAMP, $keyspace_hits, $keyspace_misses, $hit_ratio" >> $RESULTS_DIR/redis_hit_ratio.csv
else
    echo "获取Redis信息失败，API响应: $redis_info"
fi

echo -e "\n所有数据已保存到 $RESULTS_DIR 目录" 