LOGFILE=/tmp/log/mylog.txt
PROP=rest.properties

source ./commonproc.sh
source ./env.sh

touchlogfile

checkvars() {
    required_listofvars PORT RESOURCE USER PASSWORD URL WDIR
}

createprop() {

log "Database type: $DB"

case $DB in 
  db2) JFILE=db2
    DRIVERC=com.ibm.db2.jcc.DB2Driver
    ;;
     
  postgres) JFILE=postgres
    DRIVERC=org.postgresql.Driver
    ;;

  mysql) JFILE=mysql
    DRIVERC=com.mysql.jdbc.Driver
    ;;


  *) logfail "$DB - unrecognized database type, expected: db2, popstgres or mysql" ;;
esac


log "Looking for JDBC jar file according to $JFILE"
JDBCFILE=$(ls *$JFILE*)
[ $? -ne 0 ] && logfail "Cannot find JDBC jar file"
JAR=$(ls *restapijdbc*)
[ $? -ne 0 ] && logfail "Cannot find restapijdbc jar file"

cat << EOF > $PROP
jdir=$RESOURCE/restdir
plugins=RESOURCE,SQL,PYTHON3
resdir=$RESOURCE/resoudir
pythonhome=$RESOURCE/python

url=$URL
user=$USER
password=$PASSWORD
driverclass=$DRIVERC
jdbcjar=$WDIR/$JDBCFILE
EOF
}

run() {
    execute_withlog "exec java -cp $JDBCFILE:$JAR RestMain -c $PROP -p $PORT"
}


checkvars
createprop
run

#exec sleep infinity
