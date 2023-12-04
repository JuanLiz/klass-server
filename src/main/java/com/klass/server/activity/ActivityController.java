package com.klass.server.activity;

import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    MongoTemplate mongoTemplate;

    //=== Lookup aggregations for GET methods ===//

    // Embed completedBy students in activity
    LookupOperation lookupCompletedBy = LookupOperation.newLookup()
            .from("users")
            .localField("completedBy")
            .foreignField("_id")
            .as("completedBy");

    // Embed student in submission
    UnwindOperation unwindSubmissions = Aggregation.unwind("submissions", true);

    LookupOperation lookupSubmissionStudent = LookupOperation.newLookup()
            .from("users")
            .localField("submissions.student")
            .foreignField("_id")
            .as("submissions.student");

    UnwindOperation unwindSubmissionStudent = Aggregation.unwind("submissions.student", true);

    GroupOperation groupSubmissions = Aggregation.group("_id")
            .first("type").as("type")
            .first("name").as("name")
            .first("content").as("content")
            .first("enabled").as("enabled")
            .first("completedBy").as("completedBy")
            .push("submissions").as("submissions")
            .first("openDate").as("openDate")
            .first("dueDate").as("dueDate");

    //=== REST methods ===//

    // Get all activities
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ActivityProjection> getAllActivities() {

        Aggregation aggregation = Aggregation.newAggregation(
                // Student in submission
                unwindSubmissions,
                lookupSubmissionStudent,
                unwindSubmissionStudent,
                groupSubmissions,
                // CompletedBy students
                lookupCompletedBy
        );

        return mongoTemplate.aggregate(
                aggregation,
                "activities",
                ActivityProjection.class
        ).getMappedResults();
    }

    // Get activity by id
    @GetMapping("/{activityId}")
    @Nullable
    public Optional<ActivityProjection> getActivityById(@PathVariable String activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(activityId)),
                // Student in submission
                unwindSubmissions,
                lookupSubmissionStudent,
                unwindSubmissionStudent,
                groupSubmissions,
                // CompletedBy students
                lookupCompletedBy
        );

        return mongoTemplate.aggregate(
                aggregation,
                "activities",
                ActivityProjection.class
        ).getMappedResults().stream().findFirst();
    }

    @PreAuthorize("hasRole('ADMIN') || hasRole('INSTRUCTOR')")
    @PostMapping
    public Activity createActivity(@RequestBody Activity activity) {
        return activityRepository.save(activity);
    }

    @PreAuthorize("hasRole('ADMIN') || hasRole('INSTRUCTOR')")
    @PutMapping("/{activityId}")
    public Activity updateActivity(@PathVariable String activityId, @RequestBody Activity activity) {
        activity.setId(activityId);
        return activityRepository.save(activity);
    }

    @PreAuthorize("hasRole('ADMIN') || hasRole('INSTRUCTOR')")
    @DeleteMapping("/{activityId}")
    public void deleteActivity(@PathVariable String activityId) {
        activityRepository.deleteById(activityId);
    }

    // Additional methods

    // Enable/disable activity
    @PreAuthorize("hasRole('ADMIN') || hasRole('INSTRUCTOR')")
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
    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{activityId}/completed/{studentId}")
    public void addStudentToCompletedBy(@PathVariable String activityId, @PathVariable ObjectId studentId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            Activity activityToUpdate = activity.get();
            List<ObjectId> completedBy = activityToUpdate.getCompletedBy();
            // Add if not already in list
            if (!completedBy.contains(studentId)) {
                completedBy.add(studentId);
            }
            activityToUpdate.setCompletedBy(completedBy);
            activityRepository.save(activityToUpdate);
        }
    }

    // Remove student from completedBy (mark as incomplete)
    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{activityId}/completed/{studentId}")
    public void removeStudentFromCompletedBy(@PathVariable String activityId, @PathVariable ObjectId studentId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            Activity activityToUpdate = activity.get();
            List<ObjectId> completedBy = activityToUpdate.getCompletedBy();
            completedBy.removeIf(student -> student.equals(studentId));
            activityToUpdate.setCompletedBy(completedBy);
            activityRepository.save(activityToUpdate);
        }
    }

    // TODO Submissions projection

    // Add or update submission to assignment
    // TODO add validation: activity must be assignment
    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{activityId}/submissions")
    public void addSubmissionToActivity(@PathVariable String activityId, @RequestBody Submission submission) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent()) {
            Activity activityToUpdate = activity.get();
            List<Submission> submissions = activityToUpdate.getSubmissions();
            submissions.removeIf(submission1 -> submission1.getStudent().equals(submission.getStudent()));
            submissions.add(submission);
            activityToUpdate.setSubmissions(submissions);
            activityRepository.save(activityToUpdate);
            // Mark as completed
            addStudentToCompletedBy(activityId, submission.getStudent());
        }
    }

    // Read submission from assignment
    @GetMapping("/{activityId}/submissions/{studentId}")
    @Nullable
    public Optional<Submission> getSubmissionFromActivity(@PathVariable String activityId, @PathVariable ObjectId studentId) {
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

}
