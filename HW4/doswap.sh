#/bin/bash
#a script to bring up web2, swap web1 with web2, then kills the old web1 container.

    #Brings up web2, which is the new activity image to be swapped in place of the old one.
    #It is passed as an argument into the script, and inserted where "$1" is at the end
    docker run -d -P --name web2 --link ecs189_proxy_1 --net ecs189_default $1


    #runs the swap script to swap from web1 to web2
    docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh


    #removes the unused container web1 so that only two containers are running at once.
    ID=$(docker ps -aqf "name=web1")
    docker rm -f $ID

    #renames container web2 to web1 so if this script is ever ran again, it can do so without any problems.
    docker rename web2 web1
