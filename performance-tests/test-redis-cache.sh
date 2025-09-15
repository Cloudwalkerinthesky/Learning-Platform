#!/bin/bash

# 定义测试参数
TEST_ITERATIONS=100
CACHE_ENDPOINT="http://localhost:1238/api/courses/list"
CACHE_CLEAR_ENDPOINT="http://localhost:1238/api/admin/cache/clear"
RESULT_DIR="./logs/cache_test"
SUMMARY_FILE="$RESULT_DIR/cache_performance.csv"

# 创建结果目录
mkdir -p $RESULT_DIR

# 初始化摘要文件
echo "请求类型,平均响应时间(ms),最大响应时间(ms),最小响应时间(ms),标准差(ms)" > $SUMMARY_FILE

echo "开始Redis缓存性能测试..."

# 测试函数：发送请求并测量响应时间
measure_response_time() {
    local endpoint=$1
    local label=$2
    local result_file="$RESULT_DIR/${label}_results.csv"
    
    echo "请求ID,响应时间(ms),状态码" > $result_file
    
    local total_time=0
    local max_time=0
    local min_time=9999
    local times=()
    
    echo "执行 $label 测试 ($TEST_ITERATIONS 次请求)..."
    
    for ((i=1; i<=$TEST_ITERATIONS; i++)); do
        # 使用curl测量响应时间
        local start_time=$(date +%s%N)
        local http_code=$(curl -s -o /dev/null -w "%{http_code}" $endpoint)
        local end_time=$(date +%s%N)
        
        # 计算响应时间（毫秒）
        local response_time=$(( (end_time - start_time) / 1000000 ))
        
        # 保存结果
        echo "$i,$response_time,$http_code" >> $result_file
        
        # 更新统计数据
        total_time=$((total_time + response_time))
        times+=($response_time)
        
        if (( response_time > max_time )); then
            max_time=$response_time
        fi
        
        if (( response_time < min_time )); then
            min_time=$response_time
        fi
        
        # 每10次请求显示进度
        if (( i % 10 == 0 )); then
            echo "  已完成 $i/$TEST_ITERATIONS 次请求"
        fi
        
        # 简单延迟，避免过快请求
        sleep 0.1
    done
    
    # 计算平均响应时间
    local avg_time=$(( total_time / TEST_ITERATIONS ))
    
    # 计算标准差
    local sum_squared_diff=0
    for time in "${times[@]}"; do
        local diff=$((time - avg_time))
        sum_squared_diff=$((sum_squared_diff + diff*diff))
    done
    local std_dev=$(bc -l <<< "scale=2; sqrt($sum_squared_diff / $TEST_ITERATIONS)")
    
    # 写入摘要
    echo "$label,$avg_time,$max_time,$min_time,$std_dev" >> $SUMMARY_FILE
    
    echo "完成 $label 测试：平均响应时间 ${avg_time}ms (最小: ${min_time}ms, 最大: ${max_time}ms)"
    
    # 返回平均时间
    echo $avg_time
}

# 模拟Redis缓存测试方法（如果无法直接访问Redis）
simulate_redis_test() {
    # 第一阶段：清除缓存并测量冷启动性能
    echo "清除缓存..."
    curl -s $CACHE_CLEAR_ENDPOINT > /dev/null
    
    # 测量冷缓存性能（缓存未命中）
    cold_avg=$(measure_response_time "$CACHE_ENDPOINT" "cold_cache")
    
    # 第二阶段：测量热缓存性能
    echo "缓存已预热，开始测量热缓存性能..."
    warm_avg=$(measure_response_time "$CACHE_ENDPOINT" "warm_cache")
    
    # 计算性能提升
    improvement=$(bc -l <<< "scale=2; ($cold_avg - $warm_avg) * 100 / $cold_avg")
    
    echo "缓存性能测试完成！"
    echo "冷缓存平均响应时间: ${cold_avg}ms"
    echo "热缓存平均响应时间: ${warm_avg}ms"
    echo "性能提升: ${improvement}%"
    
    # 保存性能提升数据
    echo "缓存性能提升率(%)" > "$RESULT_DIR/cache_improvement.csv"
    echo "$improvement" >> "$RESULT_DIR/cache_improvement.csv"
}

# 如果可以访问Redis，直接测量缓存命中率
measure_redis_cache_hit_rate() {
    # 假设我们有一个管理端点可以提供缓存命中率统计
    local stats_endpoint="http://localhost:1238/api/admin/cache/stats"
    
    echo "获取Redis缓存统计信息..."
    local stats=$(curl -s $stats_endpoint)
    
    # 提取缓存命中率和其他统计数据
    # 实际情况下应该解析JSON响应
    echo "Redis缓存统计信息: $stats"
    
    # 将数据写入文件
    echo "$stats" > "$RESULT_DIR/redis_stats.json"
}

# 执行测试
simulate_redis_test

# 尝试获取Redis缓存命中率（如果支持）
if curl -s -o /dev/null -w "%{http_code}" $CACHE_CLEAR_ENDPOINT | grep -q "200"; then
    measure_redis_cache_hit_rate
else
    echo "无法访问Redis缓存管理端点，跳过缓存命中率测量"
fi

echo "Redis缓存性能测试完成，结果保存在: $RESULT_DIR"

# 创建图表数据
echo "准备图表数据..."
paste -d ',' <(echo "响应时间类型"; echo "冷缓存"; echo "热缓存") \
             <(echo "响应时间(ms)"; grep "cold_cache" $SUMMARY_FILE | cut -d',' -f2; grep "warm_cache" $SUMMARY_FILE | cut -d',' -f2) \
    > "$RESULT_DIR/chart_data.csv"

echo "全部测试完成!" 