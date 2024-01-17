package com.klass.server.activity;

import com.mongodb.lang.Nullable;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedList;
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

    // All aggregations ordered
    LinkedList<AggregationOperation> activityAggregations = new LinkedList<>(List.of(
            // Student in submission
            unwindSubmissions,
            lookupSubmissionStudent,
            unwindSubmissionStudent,
            groupSubmissions,
            // CompletedBy students
            lookupCompletedBy
    ));


    //=== REST methods ===//

    // TODO: Activity preview projection

    // Get all activities
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ActivityProjection>> getAllActivities() {
        return ResponseEntity.ok(mongoTemplate.aggregate(
                Aggregation.newAggregation(activityAggregations),
                "activities",
                ActivityProjection.class
        ).getMappedResults());
    }

    // Get activity by id
    @GetMapping("/{activityId}")
    @Nullable
    public ResponseEntity<ActivityProjection> getActivityById(@PathVariable String activityId) {

        LinkedList<AggregationOperation> aggregationList = activityAggregations;
        aggregationList.addFirst(Aggregation.match(Criteria.where("_id").is(activityId)));

        try {
            return ResponseEntity.ok(mongoTemplate.aggregate(
                    Aggregation.newAggregation(aggregationList),
                    "activities",
                    ActivityProjection.class
            ).getUniqueMappedResult());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create activity
    @PreAuthorize("hasRole('ADMIN') || hasRole('INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody @Valid Activity activity, UriComponentsBuilder uriComponentsBuilder) {
        Activity newActivity = activityRepository.save(activity);
        URI url = uriComponentsBuilder.path("/activities/{id}")
                .buildAndExpand(newActivity.getId())
                .toUri();
        return ResponseEntity.created(url).body(newActivity);
    }

    // Update activity
    @PreAuthorize("hasRole('ADMIN') || hasRole('INSTRUCTOR')")
    @PutMapping("/{activityId}")
    public ResponseEntity<Activity> updateActivity(@PathVariable String activityId, @RequestBody @Valid Activity activity) {
        activity.setId(activityId);
        return ResponseEntity.ok(activityRepository.save(activity));
    }

    // Delete activity
    @PreAuthorize("hasRole('ADMIN') || hasRole('INSTRUCTOR')")
    @DeleteMapping("/{activityId}")
    public ResponseEntity deleteActivity(@PathVariable String activityId) {
        activityRepository.deleteById(activityId);
        return ResponseEntity.noContent().build();
    }

    // Additional methods

    // Enable/disable activity
    @PreAuthorize("hasRole('ADMIN') || hasRole('INSTRUCTOR')")
    @PutMapping("/{activityId}/availability")
    public ResponseEntity enableActivity(@PathVariable String activityId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            Activity activityToUpdate = activity.get();
            activityToUpdate.setEnabled(!activityToUpdate.isEnabled());
            activityRepository.save(activityToUpdate);
            return ResponseEntity.ok().build();
        }

    }

    // Completed

    // Add student to completedBy (mark as completed)
    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{activityId}/completed/{studentId}")
    public ResponseEntity addStudentToCompletedBy(@PathVariable String activityId, @PathVariable ObjectId studentId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            Activity activityToUpdate = activity.get();
            List<ObjectId> completedBy = activityToUpdate.getCompletedBy();
            // Add if not already in list
            if (!completedBy.contains(studentId)) {
                completedBy.add(studentId);
            }
            activityToUpdate.setCompletedBy(completedBy);
            activityRepository.save(activityToUpdate);
            return ResponseEntity.ok().build();
        }
    }

    // Remove student from completedBy (mark as incomplete). TODO Deprecation with front
    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{activityId}/completed/{studentId}")
    public ResponseEntity removeStudentFromCompletedBy(@PathVariable String activityId, @PathVariable ObjectId studentId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            Activity activityToUpdate = activity.get();
            List<ObjectId> completedBy = activityToUpdate.getCompletedBy();
            completedBy.removeIf(student -> student.equals(studentId));
            activityToUpdate.setCompletedBy(completedBy);
            activityRepository.save(activityToUpdate);
            return ResponseEntity.ok().build();
        }
    }

    // TODO Submissions projection

    // Add or update submission to assignment
    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{activityId}/submissions")
    public ResponseEntity addSubmissionToActivity(@PathVariable String activityId, @RequestBody Submission submission) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isPresent() && activity.get().getType().equals("assign")) {
            Activity activityToUpdate = activity.get();
            List<Submission> submissions = activityToUpdate.getSubmissions();
            submissions.removeIf(submission1 -> submission1.getStudent().equals(submission.getStudent()));
            submissions.add(submission);
            activityToUpdate.setSubmissions(submissions);
            activityRepository.save(activityToUpdate);
            // Mark as completed
            addStudentToCompletedBy(activityId, submission.getStudent());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Read submission from assignment
    @GetMapping("/{activityId}/submissions/{studentId}")
    @Nullable
    public ResponseEntity<Submission> getSubmissionFromActivity(@PathVariable String activityId, @PathVariable ObjectId studentId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            Activity activityToRead = activity.get();
            List<Submission> submissions = activityToRead.getSubmissions();

            // Return submission searching student
            return submissions.stream()
                    .filter(submission -> submission.getStudent().equals(studentId))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

    }

}
