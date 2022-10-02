#!/usr/bin/env bash

function find_idle_profile() {
  RESPONSE_CODE=$(curl -s -o /dev/null -w "${http_code}" http://localhost/app-profile)
  # 현재 Nginx 가 바라보고 있는 스프링 부터가 정상적으로 수행 중인지 확인
  # 응답값을 HttpStatus 로 받는다.

  # 정상이면 200, 오류가 발생하면 400~503 이므로 400 이상을 예외로 보고 real2 를 사용합니다.
  if [ ${RESPONSE_CODE} -ge 400 ]
  then
    CURRENT_PROFILE=real2
  else
    CURRENT_PROFILE=$(curl -s http://localhost/app-profile)
  fi

  if [ ${CURRENT_PROFILE} == real1 ]
  then
    IDLE_PROFILE=real2
  else
    IDLE_PROFILE=real1
  fi

  # bash script 는 값을 반환하는 기능이 없다.
  # 그래서 마지막 줄에서 echo 로 출력 후, 클라에서 그 값을 잡아서 사용한다. $(find_idle_profile)
  # 꼭 마지막에 출력해야 한다.
  echo "${IDLE_PROFILE}"
}

# find the port of idle profile
function find_idle_port() {
  IDLE_PROFILE=$(find_idle_profile)

  if [ ${IDLE_PROFILE} == real1 ]
  then
    echo "8081"
  else
    echo "8082"
  fi
}