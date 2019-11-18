FROM centos:latest
MAINTAINER AnilG
RUN yum install -y https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm
RUN yum install -y stress
CMD ["stress","--verbose","--cpu", "8", "--io","4","--vm","2","--vm-bytes","128M","--timeout","10m"]
