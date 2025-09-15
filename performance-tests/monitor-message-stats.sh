#!/bin/bash

# 配置API地址
API_URL="http://localhost:1238/api/learning/monitoring/message-stats"

# 获取消息统计数据
echo "正在获取消息处理统计数据..."
response=$(curl -s $API_URL)

# 检查响应
if [[ $response == *"\"code\":200"* ]]; then
    # 提取数据
    total=$(echo $response | grep -o '"total":[0-9]*' | cut -d':' -f2)
    success=$(echo $response | grep -o '"success":[0-9]*' | cut -d':' -f2)
    rate=$(echo $response | grep -o '"rate":"[^"]*"' | cut -d'"' -f4)
    
    echo "消息处理统计："
    echo "总消息数：$total"
    echo "成功处理数：$success"
    echo "成功率：$rate"
    
    # 保存到文件
    echo "$(date '+%Y-%m-%d %H:%M:%S'), $total, $success, $rate" >> ./logs/message-stats.csv
else
    echo "获取数据失败，API响应: $response"
fi 