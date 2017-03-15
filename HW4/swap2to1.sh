#/bin/bash
#a script to bring up web1, swap web2 with web1, then kills the old web2 container.


#Starts compose yml again, to get web1 container up and running
docker-compose -p ecs189 up  &


#runs the swap script to swap from web2 to web1
sleep 10 && docker exec ecs189_proxy_1 /bin/bash /bin/swap1.sh


#removes the unused container web1 so that only two containers are running at once.
ID=$(docker ps -aqf "name=ecs189_web2_1")
docker rm $ID -f