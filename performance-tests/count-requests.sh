#!/bin/bash

# 设置日志文件位置
LOG_FILE="../api-gateway/logs/gateway-access.log"

# 获取今天的日期 (格式: 26/Apr/2025)
TODAY=$(date +"%d/%b/%Y")

# 统计今天的总请求数
echo "今日总请求数统计："
grep "$TODAY" $LOG_FILE | wc -l

# 按服务统计请求数
echo -e "\n按服务统计请求数："
echo "用户服务请求数："
grep "$TODAY" $LOG_FILE | grep "/user-service/" | wc -l

echo "课程服务请求数："
grep "$TODAY" $LOG_FILE | grep "/course-service/" | wc -l

echo "学习服务请求数："
grep "$TODAY" $LOG_FILE | grep "/learning-service/" | wc -l

echo "评论服务请求数："
grep "$TODAY" $LOG_FILE | grep "/comment-service/" | wc -l

# 统计独立IP数量（近似活跃用户数）
echo -e "\n今日独立IP数（活跃用户数）："
grep "$TODAY" $LOG_FILE | awk '{print $1}' | sort | uniq | wc -l

# 统计每小时请求数分布
echo -e "\n每小时请求数分布："
grep "$TODAY" $LOG_FILE | awk '{print $4}' | cut -d: -f2 | sort | uniq -c | sort -n

# 分析响应时间
echo -e "\n响应时间统计（毫秒）："
echo "平均响应时间："
grep "$TODAY" $LOG_FILE | awk '{print $NF}' | awk '{sum+=$1} END {print sum/NR/1000000 "ms"}'

echo "最大响应时间："
grep "$TODAY" $LOG_FILE | awk '{print $NF}' | sort -n | tail -1 | awk '{print $1/1000000 "ms"}'

echo "最小响应时间："
grep "$TODAY" $LOG_FILE | awk '{print $NF}' | sort -n | head -1 | awk '{print $1/1000000 "ms"}' 