#/bin/bash
#a script to bring up web2, swap web1 with web2, then kills the old web1 container.


#Starts compose yml again, to get web2 container up and running
docker-compose -p ecs189 up  &


#runs the swap script to swap from web1 to web2
sleep 10 && docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh


#removes the unused container web1 so that only two containers are running at once.
ID=$(docker ps -aqf "name=ecs189_web1_1")
docker rm $ID -f