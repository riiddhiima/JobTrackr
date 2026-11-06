package com.jobtrackr.job;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Spring generates the implementation: save, findById, findAll, delete... */
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // Both filters are optional: a null parameter means "don't filter on this".
    @Query("""
            select a from JobApplication a
            where (:status is null or a.status = :status)
              and (:company is null or lower(a.company) like lower(concat('%', :company, '%')))
            order by a.appliedOn desc, a.id desc
            """)
    List<JobApplication> search(@Param("status") ApplicationStatus status,
                                @Param("company") String company);

    // A "derived query": Spring builds the SQL from the method name alone.
    List<JobApplication> findByStatusInAndFollowUpOnLessThanEqual(Collection<ApplicationStatus> statuses,
                                                                  LocalDate date);
}
