echo $JAVA_HOME
inst=$1
data=$2
reg=$3
config=$4
result=$5

./gradlew run --args="$inst $data $reg $config $result"