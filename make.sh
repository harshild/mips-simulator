if [ "$#" -ne 5 ]; then
    echo "Usage:"
    echo "sh make.sh inst.txt data.txt reg.txt config.txt result.tx"
fi

if [ "$#" -eq 5 ]; then
  inst=$1
  data=$2
  reg=$3
  config=$4
  result=$5

  ./gradlew run --args="$inst $data $reg $config $result"
fi
