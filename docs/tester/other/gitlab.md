# GitLab CI/CD集成

## 概述

通过 GitLab CI/CD 可以实现自动化测试，生成测试报告，并自动发布测试报告到Gitlab Pages。

## 配置

```yaml
stages: # List of stages for jobs, and their order of execution
  - test

cache:
  key: "${CI_COMMIT_REF_SLUG}"
  paths:
    - .m2/repository/

variables:
  ALLURE_RESULTS: "target/allure-results"
  ALLURE_REPORT: "target/allure-report"
  LOGS_PATH: "logs"
  PAGES_PATH: "public"

test-job: # 执行测试并创建测试报告
  stage: test
  tags:
    - qa
  image: 包含Java21\Maven 的镜像
  only:
    - master # 仅在 master 分支上执行
  script: #通过maven 运行测试和生成allure报告
    - echo "Running unit tests..."
    - mvn test -Dallure.results.directory=$ALLURE_RESULTS || true # 忽略测试失败，以确保测试报告生成成功
  after_script:
    - mvn allure:report -Dallure.results.directory=$ALLURE_RESULTS -Dallure.report.directory=$ALLURE_REPORT
  cache:
    key: report-$CI_PIPELINE_IID
    paths:
      - $ALLURE_REPORT
    policy: push

page: # 发布测试报告到Gitlab Pages
  stage: test
  tags:
    - qa
  image: alpine:3.16
  only:
    - master # 仅在 master 分支上执行
  script:
    - echo "Deploying pages ..."
    - mkdir $PAGES_PATH
    - cp -r $ALLURE_REPORT/* $PAGES_PATH/
  needs:
    - test-job
  cache:
    key: report-$CI_PIPELINE_IID
    paths:
      - $ALLURE_REPORT
    policy: pull
  artifacts:
    paths:
      - $PAGES_PATH


```