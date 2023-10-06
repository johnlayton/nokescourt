# View Resolvers

### Design

#### Simple Model
<!--
```puml
@startuml class

class "[[https://github.com/johnlayton/nokescourt/blob/main/src/main/java/org/nokescourt/model/Course.java Course]]" as Course {
  code :: String 
}

class "[[https://github.com/johnlayton/nokescourt/blob/main/src/main/java/org/nokescourt/model/Exam.java Exam]]" as Exam {
  course :: Course
  code :: String
}

Exam -> Course

@enduml
```
-->
![](docs/class.svg)


#### Simple Access
<!--
```puml
@startuml sequence

actor Client
participant Container
participant ContentNegotiatingViewResolver
participant MustacheViewResolver
participant "[[https://github.com/johnlayton/nokescourt/blob/main/src/main/java/org/nokescourt/view/MyPdfViewResolver.java MyPdfViewResolver]]" as MyPdfViewResolver
participant "[[https://github.com/johnlayton/nokescourt/blob/main/src/main/java/org/nokescourt/controller/ExamsViewController.java ExamsViewController]]" as ExamsViewController
participant "[[https://github.com/johnlayton/nokescourt/blob/main/src/main/java/org/nokescourt/model/ExamService.java ExamService]]" as ExamService

Client -> Container : [get] /view/exam?format=html
activate Container
Container -> ExamsViewController : exams 
activate ExamsViewController
ExamsViewController -> ExamService : listExams 
activate ExamService
ExamsViewController <- ExamService 
deactivate ExamService
ExamsViewController -> Container : ModelAndView("exams")
deactivate ExamsViewController
Container -> Container : getViewResolver
Container -> ContentNegotiatingViewResolver : getView
ContentNegotiatingViewResolver -> MustacheViewResolver : getView
MustacheViewResolver -> ContentNegotiatingViewResolver
ContentNegotiatingViewResolver -> Container
Container -> Client 
deactivate Container

Client -> Container : [get] /view/exam?format=pdf
activate Container
Container -> ExamsViewController : exams 
activate ExamsViewController
ExamsViewController -> ExamService : listExams 
activate ExamService
ExamsViewController <- ExamService 
deactivate ExamService
ExamsViewController -> Container : ModelAndView("exams")
deactivate ExamsViewController
Container -> Container : getViewResolver
Container -> ContentNegotiatingViewResolver : getView
ContentNegotiatingViewResolver -> MyPdfViewResolver : getView
MyPdfViewResolver -> ContentNegotiatingViewResolver
ContentNegotiatingViewResolver -> Container
Container -> Client 
deactivate Container

@enduml
```
-->
![](docs/sequence.svg)

### Setup

```shell
devbox install
```

### Generate Documentation

```shell
devbox run plantuml -tsvg -o docs README.md
```

```shell
devbox run documentation
```

### Run

```shell
devbox 'run gradle --gradle-user-home .gradle --console=rich --build-cache bootRun'
```

```shell
devbox run server
```

### Test Rest Controller

```shell
devbox run 'curl --header "Accept: application/json" "http://localhost:8080/rest/exams"  | jq -r .'
```

```shell
devbox run 'curl --header "Accept: application/xml" "http://localhost:8080/rest/exams" | xq'
```

### Test View Controller

```shell
devbox run 'curl --header "Accept: application/json" "http://localhost:8080/view/exams" | jq -r .'
```

```shell
devbox run 'curl --header "Accept: text/plain" "http://localhost:8080/view/exams"'
```

```shell
devbox run 'curl --header "Accept: text/html" "http://localhost:8080/view/exams"'
```

```shell
devbox run 'wget --header "Accept: application/pdf" -O exams.pdf "http://localhost:8080/view/exams"'
```

```shell
open "http://localhost:8080/view/exams?format=html"
```

```shell
open "http://localhost:8080/view/exams?format=txt"
```

```shell
open "http://localhost:8080/view/exams?format=pdf"
```

```shell
open "http://localhost:8080/view/exams?format=json"
```

### Show Actuator

```shell
open "http://localhost:8080/actuator"
```
