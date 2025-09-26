## **Assignment 2: Document Similarity using MapReduce**

 **Name: Sai Harsha Vunnava**
 
 **Student ID:801418991**

**📌 Project Overview**

This project implements a Hadoop MapReduce application that computes document similarity between text files using Jaccard similarity. The program takes multiple text files as input, processes them in a distributed Hadoop cluster, and outputs similarity scores between every pair of documents.

## ⚙️ Approach and Implementation

***Mapper Design***

- Input: Each line of a document (document ID and words).

- Logic: The mapper tokenizes the words and emits intermediate key-value pairs representing document IDs and words.

- Output: Document ID as the key and words (comma-separated) as the value.

***Reducer Design***

- Input: Key = Document pair, Values = word sets.

- Logic: For each pair of documents, the reducer calculates the intersection and union of words.

- Output: Key = (doc1, doc2), Value = Jaccard similarity score (intersection / union).

***Overall Data Flow***

1. Input datasets (ds1.txt, ds2.txt, ds3.txt) are uploaded into HDFS.
2. Mapper reads and emits document-word sets.
3. Shuffle & sort groups words by document pairs.
4. Reducer calculates Jaccard similarity for each pair and writes results into HDFS output.

## Setup and Execution

### 1. *Start the Hadoop Cluster*

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d

```

### 2. *Build the Code*

Build the code using Maven:

```bash
mvn clean install
```

This generates the JAR:

```bash
target/DocumentSimilarity-0.0.1-SNAPSHOT.jar
```

### 3. *Copy JAR to Docker Container*

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
docker cp datasets/ resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 4. Connect to ResourceManager Container
```bash
docker exec -it resourcemanager /bin/bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. Set Up HDFS
```bash
hadoop fs -mkdir -p /input/dataset
hadoop fs -put ./datasets /input/dataset
```

### 6. Run the MapReduce Job
```bash
hadoop jar DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/dataset/datasets /output
```
### 7. View the Output
```bash
hadoop fs -cat /output/*
```

### ✅ Obtained Output:
```bash
(ds2.txt, ds1.txt)      -> 0.11
(ds3.txt, ds1.txt)      -> 0.03
(ds3.txt, ds2.txt)      -> 0.22
```



### 8. Copy Output Back to Local Machine

Inside the container:
```bash
hdfs dfs -get /output /opt/hadoop-3.2.1/share/hadoop/mapreduce/
exit
```

From host:
```bash
docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output/ output/
```
### ⚡ Challenges and Solutions

**Challenge: **Initial ClassNotFoundException because source files were under src/main/com/... instead of src/main/java/com/....

***Solution:*** Moved the package to the correct Maven structure (src/main/java).

***Challenge:*** JAR was empty on first build.

***Solution:*** Fixed pom.xml with proper build plugins and rebuilt with mvn clean install.

***Challenge:*** Output directory conflict in HDFS (File already exists).

***Solution:*** Used a new output folder name each run (/output, /output1, etc.).

### 📂 Sample Input

Input files (ds1.txt, ds2.txt, ds3.txt)
```bash
ds1.txt: Hadoop MapReduce is a programming model
ds2.txt: Apache Spark is faster than Hadoop MapReduce
ds3.txt: MapReduce and Spark are used for Big Data processing
```

### 📂 Sample Output (Expected)
```bash
(Document1, Document2 Similarity: 0.56)
(Document1, Document3 Similarity: 0.42)
(Document2, Document3 Similarity: 0.50)

```
### ✅ Obtained Output (from HDFS)
```bash
(ds2.txt, ds1.txt)      -> 0.11
(ds3.txt, ds1.txt)      -> 0.03
(ds3.txt, ds2.txt)      -> 0.22
```
---



## Obtained Output: (Place your obtained output here.)

***Output with 1 datanode:***

<img width="1402" height="429" alt="image" src="https://github.com/user-attachments/assets/46c3ed3e-0afd-48b4-99ab-47d9a9270740" />


***Output with 3 datanodes:***

<img width="1408" height="390" alt="image" src="https://github.com/user-attachments/assets/f28b76bf-79a9-4b2c-a173-d07b7eca918c" />

## 📊 Performance Comparison: 1 Node vs 3 Nodes

We executed the MapReduce job on a Hadoop cluster with different node configurations and observed the following differences:

| Execution Setup   | Observed Behavior | Time Taken | Notes |
|-------------------|------------------|------------|-------|
| **1 Node (Single Datanode)** | All computation and storage handled by a single datanode. Job ran successfully but slower due to no parallelism. | Higher execution time. | Suitable only for testing or very small datasets. |
| **3 Nodes (Multiple Datanodes)** | Workload distributed across three datanodes. Mapper and Reducer tasks executed in parallel, improving performance. | Noticeably lower execution time. | More scalable, efficient, and fault-tolerant. |

✅ **Conclusion:** Running the job on **3 nodes** significantly reduced execution time and showcased Hadoop’s true advantage—**distributed processing and scalability**.  
Single-node execution is useful for debugging or practice, but multi-node clusters are necessary for handling larger datasets efficiently.


## Obtained Output: (Place your obtained output here.)

**Output with 1 datanode:**

<img width="1392" height="375" alt="image" src="https://github.com/user-attachments/assets/cdab360a-4aa3-40d0-9b5c-e070c1fc7296" />

**Output with 3 datanodes:**

<img width="1381" height="362" alt="image" src="https://github.com/user-attachments/assets/80ad598f-9387-4044-bc97-bf0c8a290842" />
