# Requirements
  ## Recruiter
  * Post new job
  * View our job
  * View list of candidates that have applied for a job
  * Edit profile and upload profile photo
  ## Candidate
  * Search for job
  * Apply for a job
  * View list of jobs that you (job candidate) has applied for
  * Edit profile and upload profile photo
  * Upload resume  
  ## Common fun
  * Register for new account
  * Login / Logout

# Key Class
| Name  | Description |
| :--- | :--- |
| HomeController  | Show home page  |
| JobPostActivityController  | Managing job posts and searching job posts |
| JobSeekerApplyController  | Applying for jobs  |
| JobSeekerProfileController  | Managing job seeker profile  |
| JobSeekerSaveController  | Managing job that job seeker has applied for  |
| RecruiterProfileController  | Managing recruiter profile  |
| UsersController  | Login/Logout/Register  |

# Database Entities
| Name  | Description |
| :--- | :--- |
| JobCompany  | A job company: name, logo etc  |
| JobLocation  | A job location: city, country, state etc |
| JobPostActivity  | A job post: title, decription, salary, remote etc  |
| JobSeekerApply  | Tracks the job seekers who have applied for a job |
| JobSeekerProfile  | Info about job seeker: name, cty, state, skills etc  |
| JobSeekerSave  | Tracks the jobs a job seeker has applied to  |
| Skills  | Info about job seeker skills: Skill name and experience with years and level  |
| RecruiterProfile  | Info about recruiter: name, experience level, years of experience  |
| Users  | Info about a user: email, password etc  |
| UsersType  | Type/role of user: recruiter or job seeker  |



![image](https://github.com/user-attachments/assets/742114e5-8efe-4ad6-81be-535273a1cfd9)

