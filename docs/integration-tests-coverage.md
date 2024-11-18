
## Integration Tests Coverage
This is an additional deliverable we developed to ensure robustness for our system while developing. We covered the critical endpoints in automated integration tests.

##### Endpoints covered in the Integrations tests
- [x] http://{{ilim-url}}/admin/approve-course/{{courseId}}
- [x] http://{{ilim-url}}/admin/confirm-course-purchase
- [x] http://{{ilim-url}}/admin/reject-course/{{courseId}}
- [x] http://{{ilim-url}}/auth/sign-in
- [x] http://{{ilim-url}}/course/{{courseId}}
- [x] http://{{ilim-url}}/instructor/course/{{courseId}}/add-module
- [x] http://{{ilim-url}}/instructor/course/{{courseId}}/submit-for-approval
- [x] http://{{ilim-url}}/instructor/course/created
- [x] http://{{ilim-url}}/instructor/create-course
- [x] http://{{ilim-url}}/instructor/delete-module/{{moduleId}}
- [x] http://{{ilim-url}}/instructor/delete-quiz/{{quizId}}
- [x] http://{{ilim-url}}/instructor/delete-video/{{videoId}}
- [x] http://{{ilim-url}}/instructor/module/{{moduleId}}/add-quiz
- [x] http://{{ilim-url}}/instructor/module/{{moduleId}}/add-video
- [x] http://{{ilim-url}}/instructor/quiz-attempt/{{quizId}}
- [x] http://{{ilim-url}}/instructor/update-module/{{moduleId}}
- [x] http://{{ilim-url}}/instructor/update-quiz/{{quizId}}
- [x] http://{{ilim-url}}/instructor/update-video/{{videoId}}
- [x] http://{{ilim-url}}/module/{{moduleId}}
- [x] http://{{ilim-url}}/quiz/{{quizId}}
- [x] http://{{ilim-url}}/student/attempt-quiz
- [x] http://{{ilim-url}}/student/purchase-course/{{courseId}}
- [x] http://{{ilim-url}}/student/quiz-attempt/{{quizId}}
- [x] http://{{ilim-url}}/student/quiz-attempt/{{quizId}}/last
- [x] http://{{ilim-url}}/user
- [x] http://{{ilim-url}}/video/{{videoId}}
- [x] http://{{ilim-url}}/admin/course/all
- [x] http://{{ilim-url}}/admin/user/all
- [x] http://{{ilim-url}}/admin/user/{{userId}}
- [x] http://{{ilim-url}}/admin/instructor-application/all
- [x] http://{{ilim-url}}/admin/instructor-application/all-pending
- [x] http://{{ilim-url}}/admin/approve-instructor-application
- [x] http://{{ilim-url}}/admin/reject-instructor-application
- [x] http://{{ilim-url}}/student/instructor-application/submit
- [x] http://{{ilim-url}}/student/instructor-application
- [X] http://{{ilim-url}}/student/course/purchased

##### Not yet covered in the Integrations tests
- [ ] http://{{ilim-url}}/admin/course/wait-for-approval
- [ ] http://{{ilim-url}}/admin/block-user/{{userId}}
- [ ] http://{{ilim-url}}/admin/delete-course/{{courseId}}
- [ ] http://{{ilim-url}}/auth/change-password
- [ ] http://{{ilim-url}}/auth/reset-password
- [ ] http://{{ilim-url}}/student/course/{{courseId}}/certificate
- [ ] http://{{ilim-url}}/student/{{courseId}}
- [ ] http://{{ilim-url}}/student/course/{courseId}/check-progress
- [ ] http://{{ilim-url}}/course/published?contains={{$random.alphanumeric(8)}}
- [ ] http://{{ilim-url}}/instructor/course/{{courseId}}/reorder-modules
- [ ] http://{{ilim-url}}/instructor/update-course/{{courseId}}
- [ ] http://{{ilim-url}}/student/course/{{courseId}}/cancel-purchase
- [ ] http://{{ilim-url}}/student/course/{{courseId}}/check-purchase
- [ ] http://{{ilim-url}}/support/issues

#### Hard to be automated due to external services
- [ ] http://{{ilim-url}}/auth/sign-up
- [ ] http://{{ilim-url}}/auth/verify-account
- [ ] http://{{ilim-url}}/auth/forgot-password

#### Testing endpoints
- http://{{ilim-url}}/admin/integration-tests/demote-instructor/{{instructorId}}