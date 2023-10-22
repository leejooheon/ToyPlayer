## Commit message  
- `✅Feat`: 새로운 기능 추가  
- `🔨Fix`: 버그 수정  
- `📝Docs`: 문서 수정  
- `🧐Style`: 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우  
- `♻️Refactor`: 코드 리팩토링  
- `💡Test`: 테스트 코드, 리팩토링 테스트 코드 추가  
- `🔧Chore`: 빌드 업무 수정, 패키지 매니저 수정  
- `⬆️Update`: 버전 네임, 버전 코드 수정시  
- `🔖Release`: 릴리즈
- Reference  
  - https://www.conventionalcommits.org/  
  - https://seesparkbox.com/foundry/semantic_commit_messages  
  - http://karma-runner.github.io/1.0/dev/git-commit-msg.html  
  
## * 형상 관리 (Git)  
<img src= https://d2908q01vomqb2.cloudfront.net/7719a1c782a1ba91c031a682a0a2f8658209adbf/2019/02/12/gitflow-Page-1-3.png width=800>  

- `feature`  
     - 요구사항이나  특정  작업을  이  브랜치를  통해  진행한다.  
- `develop`  
     - feature 작업이  끝나면  이  브랜치로  통합한다.  
- `release`  
     - 여러 feature의 작업이 끝나면, develop 브랜치에서 이 브랜치로 통합한다.  
      - QA 등  무결성이  필요한  작업은  이  브랜치를  통해  진행한다.  
- `hotfix`  
     - master를  통해  브랜치를  생성한다. 배포된  버전에서  문제가  발생했을  경우  사용한다.  
- `master`  
     - 스토어에  배포된  버전 