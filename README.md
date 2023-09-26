# View Resolvers

Setup;

Simple Model

```puml

class Course {
  String code
}

class Exam {
  Course course
  String code
}

Exam -> Course
```

Simple Access

```puml
actor Client
participant Container
participant ContentNegotiatingViewResolver
participant MustacheViewResolver
participant MyPdfViewResolver
participant ExamsViewController
participant ExamService

Client --> Container : [get] /view/exam?format=html
activate Container
Container --> ExamsViewController : exams 
activate ExamsViewController
ExamsViewController --> Container : ModelAndView("exams")
deactivate ExamsViewController
Container --> Container : getViewResolver
Container --> ContentNegotiatingViewResolver : getView
ContentNegotiatingViewResolver --> MustacheViewResolver : getView
MustacheViewResolver --> ContentNegotiatingViewResolver
ContentNegotiatingViewResolver --> Container
Container --> Client 
deactivate Container

Client --> Container : [get] /view/exam?format=pdf
activate Container
Container --> ExamsViewController : exams 
activate ExamsViewController
ExamsViewController --> Container : ModelAndView("exams")
deactivate ExamsViewController
Container --> Container : getViewResolver
Container --> ContentNegotiatingViewResolver : getView
ContentNegotiatingViewResolver --> MyPdfViewResolver : getView
MyPdfViewResolver --> ContentNegotiatingViewResolver
ContentNegotiatingViewResolver --> Container
Container --> Client 
deactivate Container
```

Run;

```shell
gradlew bootRun
```

Test Rest Controller;

```shell
curl --header "Accept: application/json" "http://localhost:8080/rest/exams"
```

```shell
curl --header "Accept: application/xml" "http://localhost:8080/rest/exams"
```

Test View Controller;

```shell
curl --header "Accept: application/json" "http://localhost:8080/view/exams"
```

```shell
curl --header "Accept: text/plain" "http://localhost:8080/view/exams"
```

```shell
curl --header "Accept: text/html" "http://localhost:8080/view/exams"
```

```shell
open http://localhost:8080/view/exams?format=html
```

```shell
open http://localhost:8080/view/exams?format=pdf
```
