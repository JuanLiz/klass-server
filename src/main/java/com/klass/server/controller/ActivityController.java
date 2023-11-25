package com.klass.server.controller;

import com.klass.server.model.Activity;
import com.klass.server.model.Submission;
import com.klass.server.repository.ActivityRepository;
import com.mongodb.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @GetMapping
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @GetMapping("/{activityId}")
    @Nullable
    public Optional<Activity> getActivityById(@PathVariable String activityId) {
        return activityRepository.findById(activityId);
    }

    @PostMapping
    public Activity createActivity(@RequestBody Activity activity) {
        return activityRepository.save(activity);
    }

    @PutMapping("/{activityId}")
    public Activity updateActivity(@PathVariable String activityId, @RequestBody Activity activity) {
        activity.setId(activityId);
        return activityRepository.save(activity);
    }

    @DeleteMapping("/{activityId}")
    public void deleteActivity(@PathVariable String activityId) {
        activityRepository.deleteById(activityId);
    }

    // Additional methods

    // Enable/disable activity
    @PutMapping("/{activityId}/availability")
    public void enableActivity(@PathVariable String activityId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            Activity activityToUpdate = activity.get();
            activityToUpdate.setEnabled(!activityToUpdate.isEnabled());
            activityRepository.save(activityToUpdate);
        }
    }

    // Completed

    // Add student to completedBy (mark as completed)
    @PutMapping("/{activityId}/completed/{studentId}")
    public void addStudentToCompletedBy(@PathVariable String activityId, @PathVariable String studentId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            Activity activityToUpdate = activity.get();
            List<String> completedBy = activityToUpdate.getCompletedBy();
            // Add if not already in list
            if (!completedBy.contains(studentId)) {
                completedBy.add(studentId);
            }
            activityToUpdate.setCompletedBy(completedBy);
            activityRepository.save(activityToUpdate);
        }
    }

    // Remove student from completedBy (mark as incomplete)
    @DeleteMapping("/{activityId}/completed/{studentId}")
    public void removeStudentFromCompletedBy(@PathVariable String activityId, @PathVariable String studentId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            Activity activityToUpdate = activity.get();
            List<String> completedBy = activityToUpdate.getCompletedBy();
            completedBy.removeIf(student -> student.equals(studentId));
            activityToUpdate.setCompletedBy(completedBy);
            activityRepository.save(activityToUpdate);
        }
    }

    // TODO Submissions (assignment)

    // Add submission to assignment
    @PutMapping("/{activityId}/submissions/{studentId}")
    public void addSubmissionToActivity(@PathVariable String activityId, @PathVariable String studentId, @RequestBody Submission submission) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            Activity activityToUpdate = activity.get();
            List<Submission> submissions = activityToUpdate.getSubmissions();
            submissions.removeIf(submission1 -> submission1.getStudent().equals(studentId));
            submissions.add(submission);
            activityToUpdate.setSubmissions(submissions);
            activityRepository.save(activityToUpdate);
        }
    }

    // Read submission from assignment
    @GetMapping("/{activityId}/submissions/{studentId}")
    @Nullable
    public Optional<Submission> getSubmissionFromActivity(@PathVariable String activityId, @PathVariable String studentId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            Activity activityToRead = activity.get();
            List<Submission> submissions = activityToRead.getSubmissions();
            for (Submission submission : submissions) {
                if (submission.getStudent().equals(studentId)) {
                    return Optional.of(submission);
                }
            }
        }
        return Optional.empty();
    }

    // Remove submission from assignment (Don't implement yet)
    @DeleteMapping("/{activityId}/submissions/{studentId}")
    public void removeSubmissionFromActivity(@PathVariable String activityId, @PathVariable String studentId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            Activity activityToUpdate = activity.get();
            List<Submission> submissions = activityToUpdate.getSubmissions();
            submissions.removeIf(submission -> submission.getStudent().equals(studentId));
            activityToUpdate.setSubmissions(submissions);
            activityRepository.save(activityToUpdate);
        }
    }


}
