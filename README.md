# RestApiJSon

>  mvn package -Dmaven.test.skip=true<br>
>  mvn deploy -Dmaven.test.skip=true<br>

# Docker/Podman

## Package

> cd docker<br>
> mvn package<br>

Verify target<br>

> ll target<br>
```
-rw-r--r--. 1 sbartkowski users     2106 11-04 01:50 restapijdbc-1.0-SNAPSHOT.jar
-rw-r--r--. 1 sbartkowski users 11873250 11-05 00:55 restapijdbc-1.0-SNAPSHOT-jar-with-dependencies.jar
```
## Docker image

Any additional JDBC drivers should be put in *jdbc* directory.<br>
<br>
The docker image name is *restapijdbc*.<br>

The script is using *openjdk:11-jre-slim-buster* as the base image. The final image contains also *python3* and *JayDeBeApi* package.

> ./crimage.sh<br>
```
.......
STEP 10: WORKDIR ${WDIR}
--> 406a1bfa9ac
STEP 11: CMD ["bash","-c","./run.sh"]
STEP 12: COMMIT restapijdbc
--> 7f28f25554c
Successfully tagged localhost/restapijdbc:latest
7f28f25554c1bc4103afb07e72ce951c5209c5c4c4d583fb1e970e18f43a0396
```

## Create container

| Parameter | Description | Example
| ------- | ------------ | ------- 
| -p | 80: port exposed, can be redirected | -p 8080:80 
| -e DB | Database used. The following values are accepted: mysql, postgres, db2 | -e DB=postgres
| -e USER | Database user | -e USER=queryuser
| -e PASSWORD | User password | -e PASSWORD=secret
| -e URL | JDBC connnection string |  -e URL=jdbc:mysql://kist:3306/querydb

## PostgreSQL

>  podman run --name=mypostgres -v  /home/sbartkowski/work/restmysqlmodel/resources/:/var/resources:Z -p 8080:80 -e USER=queryuser -e PASSWORD=secret   -e DB=postgres -e URL=jdbc:postgresql://kist:5432/querydb -d restapijdbc

## MySQL/MariaDB

>  podman run --name=mymysql  -v  /home/sbartkowski/work/restmysqlmodel/resources/:/var/resources:Z -p 8080:80 -e USER=queryuser -e PASSWORD=secret   -e DB=mysql  -e URL=jdbc:mysql://kist:3306/querydb  -d restapijdbc

## DB2

To access DB2, the *restapijdbc* image is expected to contain DB2 JDBC driver jar. Before running *crimage.sh* command, the DB2 JDBC driver jar should be copied to *jdbc* directory.<br>

> podman run --name=mydb2 -v  /home/sbartkowski/work/restmysqlmodel/resources/:/var/resources:Z -p 8080:80 -e USER=db2inst1  -e PASSWORD=secret123   -e DB=db2 -e URL=jdbc:db2://thinkde:50000/querydb  -d restapijdbc
