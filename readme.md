# spark-CZE
 
**updated:** May - 2021

## Introduction

This project is aimed to show a simple 2 way ETL 

1) load data from csv file to MSSQL
2) query MSSQL, filter and save to csv file

## Prerequisites

You need to configure at least the first 3 environments to run the demo

a) HPECP 5.3 or K8s with Spark Operator installed

b) (optional) DataFabric or External Storage Solution

d) (optional) sbt, if we want to package the project, else we can just leverage the prebuilt .jar

d.1) If you plan to build your jar artifacts, on any machine you can install sbt
```
curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
sudo yum install sbt -y
```

once you have completed the prerequisites, you now have following environments:

* a) **k-master** = K8's master node where you plan to deploy the demo
* b) (optional)  **any-machine** = Any machine with CentOS or RHEL to build jar artifacts

## Setting up the Demo Index

Set-up Steps:

1. Clone Project

2. (optional) Build Artifacts

3. Customize Yaml Definitions

4. Deploy

## Setting up the Demo Extended

1.
```
git clone https://github.com/DarianHarrison/spark-CZE
```

2. (optional) On **any-machine** where you installed sbt 
note: job should take about 2 - 5 mins to build
```
sbt clean package assembly
...

```
```

...
[success] Total time: 14 s, completed Jun 11, 2020 11:08:50 PM
[info] Strategy 'concat' was applied to 2 files (Run the task at debug level to see details)
[info] Strategy 'discard' was applied to 595 files (Run the task at debug level to see details)
[info] Strategy 'first' was applied to 7071 files (Run the task at debug level to see details)
[success] Total time: 61 s (01:01), completed Jun 11, 2020 11:09:50 PM
```
this should create a fat **xxx-assembly-1.0.0.jar** that can be found in the following directory
```
ls target/scala-2.11/
```

3. On **controller-node** or **any data fabric node**

copy generated jar and sample data to the tenantShare
```
"maprfs:///tenant*********/generic-assembly-1.0.0.jar"
data/in.csv

mkdir for app logs
```
on the same data fabric image:
copy the cybersecurity-assembly-1.0.0.jar that was generated in **any-machine** and the sample data into the data fabric
```
mkdir /mapr/mnt/hcp.mapr.cluster/apps/cybersecurity
scp -r <user>@<host>:/path/to/ess-cybersecurity/shared_resources /mapr/mnt/hcp.mapr.cluster/apps/cybersecurity
```

4. Deployment

Before deploying K8's applications, you need to modify each of the following files in 

**path/to/ess-cybersecurity/kube/**

* (optional) simple-train.yaml
* df-dataingestion.yaml
* simple-inference.yaml

fields to modify (lines with commens):

* name
* namespace
* spark.mapr.user.secret
* spark.eventLog.dir
* mainClass
* mainApplicationFile
* serviceAccount

note: we have a pretrained model already on the data fabric, so this step can be skipped
```
kubectl apply -f ess-cybersecurity/kube/simple-train.yaml
kubectl logs -f pod/simple-train-driver -n <namespace>
```