source ./env.rc
echo $TMPFILE
echo $subject
$CADIR/ca.sh makecert "$subject" $TMPFILE