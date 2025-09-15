#!/bin/bash

# 定义测试参数
TEST_ITERATIONS=50
REQUEST_DELAY=0.1 # 请求间隔时间（秒）
# 定义断路器测试的端点
NORMAL_ENDPOINT="http://localhost:1238/api/courses/list"
SLOW_ENDPOINT="http://localhost:1238/api/test/slow-response"
ERROR_ENDPOINT="http://localhost:1238/api/test/error-response"
CIRCUIT_STATUS_ENDPOINT="http://localhost:1238/actuator/circuitbreakers"

# 结果目录
RESULT_DIR="./logs/circuit_breaker_test"
SUMMARY_FILE="$RESULT_DIR/circuit_breaker_summary.csv"

# 创建结果目录
mkdir -p $RESULT_DIR

# 初始化摘要文件
echo "测试类型,成功请求数,失败请求数,请求总数,平均响应时间(ms),断路器状态" > $SUMMARY_FILE

echo "开始断路器性能测试..."

# 验证端点是否可访问
validate_endpoint() {
    local endpoint=$1
    local status=$(curl -s -o /dev/null -w "%{http_code}" $endpoint)
    
    if [[ $status -ge 200 && $status -lt 500 ]]; then
        return 0 # 端点可访问
    else
        return 1 # 端点不可访问
    fi
}

# 获取断路器状态
get_circuit_status() {
    local service_name=$1
    
    # 尝试从Actuator端点获取断路器状态
    if validate_endpoint $CIRCUIT_STATUS_ENDPOINT; then
        local circuit_status=$(curl -s $CIRCUIT_STATUS_ENDPOINT | grep -o "\"$service_name\":{[^}]*\"state\":\"[^\"]*\"" | grep -o "\"state\":\"[^\"]*\"" | cut -d'"' -f4)
        
        if [[ -z "$circuit_status" ]]; then
            echo "UNKNOWN"
        else
            echo $circuit_status
        fi
    else
        echo "ENDPOINT_NOT_AVAILABLE"
    fi
}

# 执行断路器测试的主函数
test_circuit_breaker() {
    local endpoint=$1
    local test_type=$2
    local service_name=$3
    local result_file="$RESULT_DIR/${test_type}_results.csv"
    
    echo "请求ID,响应时间(ms),状态码" > $result_file
    
    local success_count=0
    local failure_count=0
    local total_time=0
    
    echo "执行 $test_type 测试 ($TEST_ITERATIONS 次请求)..."
    
    for ((i=1; i<=$TEST_ITERATIONS; i++)); do
        # 在每次请求前，检查断路器状态
        local circuit_status_before=$(get_circuit_status $service_name)
        
        # 发送请求并测量响应时间
        local start_time=$(date +%s%N)
        local response=$(curl -s -w "\n%{http_code}" $endpoint)
        local end_time=$(date +%s%N)
        
        # 提取HTTP状态码
        local http_code=$(echo "$response" | tail -n 1)
        
        # 计算响应时间（毫秒）
        local response_time=$(( (end_time - start_time) / 1000000 ))
        
        # 保存结果
        echo "$i,$response_time,$http_code" >> $result_file
        
        # 统计成功和失败的请求
        if [[ $http_code -ge 200 && $http_code -lt 500 ]]; then
            success_count=$((success_count + 1))
            total_time=$((total_time + response_time))
        else
            failure_count=$((failure_count + 1))
        fi
        
        # 显示进度
        if (( i % 10 == 0 )); then
            echo "  已完成 $i/$TEST_ITERATIONS 次请求"
            echo "  当前断路器状态: $(get_circuit_status $service_name)"
        fi
        
        # 等待一段时间，避免请求过快
        sleep $REQUEST_DELAY
    done
    
    # 检查最终断路器状态
    local final_circuit_status=$(get_circuit_status $service_name)
    
    # 计算平均响应时间（只考虑成功请求）
    local avg_time=0
    if (( success_count > 0 )); then
        avg_time=$(( total_time / success_count ))
    fi
    
    # 写入摘要
    echo "$test_type,$success_count,$failure_count,$TEST_ITERATIONS,$avg_time,$final_circuit_status" >> $SUMMARY_FILE
    
    echo "完成 $test_type 测试"
    echo "  成功请求: $success_count"
    echo "  失败请求: $failure_count"
    echo "  平均响应时间: ${avg_time}ms"
    echo "  断路器状态: $final_circuit_status"
}

# 执行正常服务测试
if validate_endpoint $NORMAL_ENDPOINT; then
    test_circuit_breaker $NORMAL_ENDPOINT "normal_service" "courseService"
else
    echo "警告: 正常服务端点不可用，跳过正常服务测试"
fi

# 执行慢响应服务测试
if validate_endpoint $SLOW_ENDPOINT; then
    test_circuit_breaker $SLOW_ENDPOINT "slow_service" "slowResponseService"
else
    echo "警告: 慢响应服务端点不可用，跳过慢响应测试"
fi

# 执行错误响应服务测试
if validate_endpoint $ERROR_ENDPOINT; then
    test_circuit_breaker $ERROR_ENDPOINT "error_service" "errorResponseService"
else
    echo "警告: 错误响应服务端点不可用，跳过错误响应测试"
fi

# 断路器恢复测试
test_circuit_recovery() {
    echo "开始断路器恢复测试..."
    
    local service_name="recoveryTestService"
    local result_file="$RESULT_DIR/recovery_test_results.csv"
    
    echo "阶段,请求状态,响应时间(ms),断路器状态" > $result_file
    
    # 第一阶段：触发断路器打开
    echo "阶段1: 触发断路器打开 - 发送请求到错误端点"
    
    for ((i=1; i<=20; i++)); do
        local start_time=$(date +%s%N)
        local http_code=$(curl -s -o /dev/null -w "%{http_code}" $ERROR_ENDPOINT)
        local end_time=$(date +%s%N)
        
        local response_time=$(( (end_time - start_time) / 1000000 ))
        local circuit_status=$(get_circuit_status $service_name)
        
        local status="成功"
        if [[ $http_code -ge 500 ]]; then
            status="失败"
        fi
        
        echo "触发阶段,$status,$response_time,$circuit_status" >> $result_file
        
        if [[ "$circuit_status" == "OPEN" ]]; then
            echo "  断路器已打开，继续下一阶段"
            break
        fi
        
        sleep $REQUEST_DELAY
    done
    
    # 第二阶段：等待断路器半开
    echo "阶段2: 等待断路器半开状态"
    local wait_time=0
    local max_wait=60
    
    while [[ $wait_time -lt $max_wait ]]; do
        local circuit_status=$(get_circuit_status $service_name)
        
        echo "等待阶段,N/A,N/A,$circuit_status" >> $result_file
        
        if [[ "$circuit_status" == "HALF_OPEN" ]]; then
            echo "  断路器已半开，继续下一阶段"
            break
        fi
        
        wait_time=$((wait_time + 5))
        echo "  等待断路器半开... (已等待 ${wait_time}s)"
        sleep 5
    done
    
    # 第三阶段：测试断路器恢复
    echo "阶段3: 测试断路器恢复 - 发送请求到正常端点"
    
    for ((i=1; i<=10; i++)); do
        local start_time=$(date +%s%N)
        local http_code=$(curl -s -o /dev/null -w "%{http_code}" $NORMAL_ENDPOINT)
        local end_time=$(date +%s%N)
        
        local response_time=$(( (end_time - start_time) / 1000000 ))
        local circuit_status=$(get_circuit_status $service_name)
        
        local status="成功"
        if [[ $http_code -ge 500 ]]; then
            status="失败"
        fi
        
        echo "恢复阶段,$status,$response_time,$circuit_status" >> $result_file
        
        if [[ "$circuit_status" == "CLOSED" ]]; then
            echo "  断路器已关闭，恢复测试完成"
            break
        fi
        
        sleep $REQUEST_DELAY
    done
    
    # 提取恢复时间数据
    local recovery_time=$(grep "恢复阶段" $result_file | head -n 1 | cut -d',' -f3)
    
    echo "断路器恢复测试完成"
    echo "  恢复时测试首次响应时间: ${recovery_time}ms"
    
    # 添加到摘要文件
    echo "断路器恢复测试,N/A,N/A,N/A,$recovery_time,已恢复到CLOSED" >> $SUMMARY_FILE
}

# 执行断路器恢复测试
if validate_endpoint $ERROR_ENDPOINT && validate_endpoint $NORMAL_ENDPOINT; then
    test_circuit_recovery
else
    echo "警告: 一个或多个必需的端点不可用，跳过断路器恢复测试"
fi

echo "断路器性能测试完成，结果保存在: $RESULT_DIR"

# 创建图表数据
echo "准备图表数据..."
paste -d ',' <(echo "服务类型"; grep "normal_service\|slow_service\|error_service" $SUMMARY_FILE | cut -d',' -f1) \
             <(echo "平均响应时间(ms)"; grep "normal_service\|slow_service\|error_service" $SUMMARY_FILE | cut -d',' -f5) \
             <(echo "成功率(%)"; awk -F',' 'NR>1 && $1 !~ /恢复/ {printf "%.1f\n", ($2/$4)*100}' $SUMMARY_FILE) \
    > "$RESULT_DIR/chart_data.csv"

echo "全部测试完成!" 