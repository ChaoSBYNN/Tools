#!/bin/bash
# 设置jar包所在的目录路径
jar_dir="/usr/local/sentinel"

start() {
    cd "$jar_dir"
    nohup java -Xms256m -Xmx512m -jar sentinel-dashboard-1.8.6.jar >/dev/null 2>&1 &
    echo "jar包已启动"
}

stop() {
    pid=$(ps aux | grep java | grep "$jar_dir" | grep -v grep | awk '{print $2}')
    if [[ -n "$pid" ]]; then
        kill -9 "$pid"
        echo "jar包已停止"
    else
        echo "jar包没有运行"
    fi
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        sleep 3
        start
        ;;
    *)
        echo "用法: $0 {start|stop|restart}"
        ;;
esac
