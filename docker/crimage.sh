DOCKERDIR=docker
IMAGENAME=restapijdbc

rm -rf $DOCKERDIR
mkdir $DOCKERDIR



downloadjar() {    
    mvn org.apache.maven.plugins:maven-dependency-plugin:get -Dartifact=$1
    mvn dependency:copy -Dartifact=$1  -DoutputDirectory=$DOCKERDIR
}

prepareresources() {
    downloadjar mysql:mysql-connector-java:8.0.27
    downloadjar org.postgresql:postgresql:42.3.1
    cp run.sh $DOCKERDIR
    cp commonproc.sh $DOCKERDIR
    cp Dockerfile $DOCKERDIR
    cp target/*dependencies* $DOCKERDIR
    [ -d jdbc ] && cp jdbc/*.jar $DOCKERDIR
}

buildimage() {
    cd $DOCKERDIR
    podman build -t $IMAGENAME .
}

prepareresources
buildimage
