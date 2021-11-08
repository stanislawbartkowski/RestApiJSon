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
| -p | 8080: port exposed, can be redirected, port is nonsecure | -p 8080:8080 
| -e DB | Database used. The following values are accepted: mysql, postgres, db2 | -e DB=postgres
| -e USER | Database user | -e USER=queryuser
| -e PASSWORD | User password | -e PASSWORD=secret
| -e URL | JDBC connnection string |  -e URL=jdbc:mysql://kist:3306/querydb
| -v Volume | */var/resource* volume should be mounted to customer resources. The custom resource directory could contain subdirectorie: *python*, *resoudir*, *restdir* | -v  /home/sbartkowski/work/restmysqlmodel/resources/:/var/resources:Z

## PostgreSQL

>  podman run --name=mypostgres -v  /home/sbartkowski/work/restmysqlmodel/resources/:/var/resources:Z -p 8080:8080 -e USER=queryuser -e PASSWORD=secret   -e DB=postgres -e URL=jdbc:postgresql://kist:5432/querydb -d restapijdbc

## MySQL/MariaDB

>  podman run --name=mymysql  -v  /home/sbartkowski/work/restmysqlmodel/resources/:/var/resources:Z -p 8080:8080 -e USER=queryuser -e PASSWORD=secret   -e DB=mysql  -e URL=jdbc:mysql://kist:3306/querydb  -d restapijdbc

## DB2

To access DB2, the *restapijdbc* image is expected to contain DB2 JDBC driver jar. Before running *crimage.sh* command, the DB2 JDBC driver jar should be copied to *jdbc* directory.<br>

> podman run --name=mydb2 -v  /home/sbartkowski/work/restmysqlmodel/resources/:/var/resources:Z -p 8080:8080 -e USER=db2inst1  -e PASSWORD=secret123   -e DB=db2 -e URL=jdbc:db2://thinkde:50000/querydb  -d restapijdbc

# OpenShift/Kubernetes

## Push image to quay.io

Image in local repository is created after running *./crimage.sh*

Tag the local image, use valid *quay.io* repository name.<br>

> podman login quay.io<br>
> podman tag restapijdbc   quay.io/stanislawbartkowski/restapijdbc:latest<br>
> podman push quay.io/stanislawbartkowski/restapijdbc:latest<br>
```
...........
Copying blob 8276ab1df2fb done  
Copying blob f73c57e2b056 done  
Copying blob 1a86b0b1243f skipped: already exists  
Copying blob 85d5baf8a9db skipped: already exists  
Copying blob f1ab4986f47c skipped: already exists  
Copying blob e81bff2725db skipped: already exists  
Copying blob bf70f8970630 done  
Copying config 8bc51ffeaa done  
Writing manifest to image destination
Copying config 8bc51ffeaa [--------------------------------------] 0.0b / 6.2KiB
Writing manifest to image destination
Storing signatures

```
## Create OpenShift project/namespace

> oc new-project restapijdbc<br>

## Create OpenShift object manually

### Create volume

*restapijdbc* service requires customized resources with REST/API definitions. Keep container and resource definition separated to allow independent updates. Adjust StorageClass and storage capacity accordingly.<br>

```
oc create -f - <<EOF
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: restapijdbc
  annotations:
    volume.beta.kubernetes.io/storage-class: "managed-nfs-storage"
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 1Mi
EOF
```

> oc get pvc<br>
```
NAME          STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS          AGE
restapijdbc   Bound    pvc-57521d05-8ddd-4c7b-94f9-d30b5898583a   1Mi        RWX            managed-nfs-storage   7s
```

### Create secret with database user and password

> oc create secret generic mysql  --from-literal USER=queryuser  --from-literal PASSWORD=secret<br>

> oc get secret<br>
```
NAME                       TYPE                                  DATA   AGE
...
mysql                      Opaque                                2      7s
```
### Create deployment

| Env variable | Description | Example
| ---- | ---- | ----- |
| DB | Database type: mysql, db2 or postgres | --env DB=mysql
| URL | JDBC connection string | -env URL="jdbc:mysql://172.30.171.241:3306/querydb"


> oc new-app --docker-image=quay.io/stanislawbartkowski/restapijdbc   --name restapijdbc  --env DB=mysql --env URL="jdbc:mysql://172.30.171.241:3306/querydb" <br>

Assign secret<br>

> oc set env deployment/restapijdbc --from secret/mysql<br>

Assign persistent volume<br>

> oc set volume deployment/restapijdbc --add --name=restapijdbc-volume-1  -t pvc --claim-name=restapijdbc --overwrite<br>

> oc get pods<br>
```
NAME                          READY   STATUS    RESTARTS   AGE
restapijdbc-b54d54cff-lzj8m   1/1     Running   0          67s
```

Verify logs<br>

> oc logs restapijdbc-b54d54cff-lzj8m<br>
```
Database type: mysql
Looking for JDBC jar file according to mysql
exec java -cp mysql-connector-java-8.0.27.jar:restapijdbc-1.0-SNAPSHOT-jar-with-dependencies.jar RestMain -c rest.properties -p 8080
Nov 06, 2021 11:16:44 AM com.rest.restservice.RestLogger info
.....
INFO: user value read:queryuser
Nov 06, 2021 11:16:45 AM com.rest.restservice.RestLogger info
INFO: password value read:XXXXXXXX
Nov 06, 2021 11:16:45 AM com.rest.runjson.executors.sql.JDBC connect
INFO: Connecting to jdbc:mysql://172.30.171.241:3306/querydb user queryuser
Nov 06, 2021 11:16:45 AM com.rest.runjson.executors.sql.JDBC connect
INFO: Connected
Nov 06, 2021 11:16:45 AM com.rest.restservice.RestLogger info
INFO: Start HTTP Server, listening on port 8080
Nov 06, 2021 11:16:45 AM com.rest.restservice.RestLogger info
INFO: Register service: {root}
```

### External access

Create NodePort<br>
```
cat <<EOF |oc apply -f -
apiVersion: v1
kind: Service
metadata:
  name: restapijdbcn
spec:
  selector:           
   deployment: restapijdbc
  type: NodePort
  ports:
  - port: 8080
    protocol: TCP
    targetPort: 8080
    nodePort: 30819
EOF
```

Configure port redirection on HAProcy host. External port is defined as 7999.<br>
> vi /etc/haproxy/haproxy.cfg<br>
```
frontend ingress-rest
        bind *:7999
        default_backend ingress-rest
        mode tcp
        option tcplog

backend ingress-rest
        balance source
        mode tcp
        server master0 10.17.43.9:30819 check
        server master1 10.17.46.40:30819 check
        server master2 10.17.48.179:30819 check
        server worker0 10.17.57.166:30819 check
        server worker1 10.17.59.104:30819 check
        server worker2 10.17.61.175:30819 check
```
> systemctl restart haproxy

## OpenShift template

All necessary object can be created through OpenShift template.<br>

<br>
> curl -s https://raw.githubusercontent.com/stanislawbartkowski/RestApiJSon/main/docker/restapijdbc.yaml | oc create -f -<br>
<br>
> oc process --parameters  restapijdbc
```
NAME                DESCRIPTION                              GENERATOR           VALUE
URL                 Database access URL string                                   
DB                  Values allowed, db2, postgres or mysql                       
USER                User to access the database                                  
PASSWORD            Password to access the database      
```

The *USER* and *PASSWORD* parameter are expected as base64 string. <br>
<br>
> echo queryuser | openssl base64<br>
```
cXVlcnl1c2VyCg==
```
> echo secret | openssl base64<br>
```
c2VjcmV0Cg==
```

Build the service.<br>

> oc new-app  restapijdbc  -p DB=mysql -p USER="cXVlcnl1c2VyCg=="  -p PASSWORD="c2VjcmV0Cg==" -p URL="jdbc:mysql://172.30.171.241:3306/querydb"<br>
```
--> Deploying template "restapijdbc/restapijdbc" to project restapijdbc

     REST/API JDBC
     ---------
     My simple implementation of configurable REST/API service

     * With parameters:
        * URL string=jdbc:mysql://172.30.171.241:3306/querydb
        * Database type=mysql
        * Database user name=cXVlcnl1c2VyCg==
        * Database Password=c2VjcmV0Cg==

--> Creating resources ...
    imagestream.image.openshift.io "restapijdbc" created
    secret "mysql" created
    persistentvolumeclaim "restapijdbc" created
    deployment.apps "restapijdbc" created
    service "restapijdbcn" created
    service "restapijdbc" created
--> Success
    Application is not exposed. You can expose services to the outside world by executing one or more of the commands below:
     'oc expose service/restapijdbcn' 
     'oc expose service/restapijdbc' 
    Run 'oc status' to view your app.
```

## Copy resource data to persistent volume using the container<br>

> oc get pods<br>
```
NAME                          READY   STATUS    RESTARTS   AGE
restapijdbc-b54d54cff-lzj8m    1/1     Running   0          67s
```

Local directory is */home/sbartkowski/work/restmysqlmodel/resources/*. 

> oc cp  /home/sbartkowski/work/restmysqlmodel/resources/ restapijdbc-b54d54cff-lzj8m:/var<br>

The command creates the following directory structure in the container.<br>
<br>
> oc rsh restapijdbc-b54d54cff-lzj8m
```
$ ls /var/resources/ -ltr
total 4
drwxr-xr-x. 2 1000700000 root   32 Nov  6 11:25 python
drwxr-xr-x. 5 1000700000 root   49 Nov  6 11:25 resoudir
drwxr-xr-x. 2 1000700000 root 4096 Nov  6 11:25 restdir
```

The resource data are stored in persistent volume. The container can be recreated without losing the configuration. Also, the resource data can be updated and rebuiding the container is not necessary.<br>

## Test

Assuming HAProxy node *kist* and port *7999*<br>

> curl  http://kist:7999/resource?resource=appdata<br>
```JSON
{
  "appname" : "Classic Model",
  "logo" : "logo192.png"
}
```

> curl  http://kist:7999/productlines<br>
```
{"res":[{"productline":"Classic Cars","image":null,"htmldescription":null,"textdescription":"Attention car enthusiasts: Make your wildest car ownership dreams come true. Whether you are looking for classic muscle cars, dream sports cars or movie-inspired miniatures, you will find great choices in this category. These replicas feature superb attention to detail and craftsmanship and offer features such as working steering system, opening forward compartment, opening rear trunk with removable spare wheel, 4-wheel independent spring suspension, and so on. The models range in size from 1:10 to 1:24 scale and include numerous limited edition and several out-of-production vehicles. All models include a certificate of authenticity from their manufacturers and come fully assembled and ready for display in the home or office."},{"productline":"Motorcycles","image":null,"htmldescription":null,"textdescription":"Our motorcycles are state of the art replicas 
...................
}
```
