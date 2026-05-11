package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.PowerAppContacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository for {@link PowerAppContacts} — the local mirror of newly-discovered
 * Dataverse service-provider contacts.
 *
 * <p>Mirrors the queries used by Laravel's
 * {@code SyncNewServiceProviders} command (dedup + status update).
 */
@Repository
public interface PowerAppContactsRepository extends JpaRepository<PowerAppContacts, Long> {

    /**
     * Laravel dedup check:
     * <pre>
     *   DB::table('powerapp_contacts')
     *     -&gt;where('powerapp_contact_id', $contactId)
     *     -&gt;orWhere('email', $email)
     *     -&gt;exists();
     * </pre>
     */
    boolean existsByPowerAppContactIdOrEmail(String powerAppContactId, String email);

    Optional<PowerAppContacts> findByPowerAppContactId(String powerAppContactId);

    /**
     * Lookup used by the Service Provider onboarding flow — the SP-onboarding token
     * (Laravel `Crypt::encrypt(email)`) decrypts to the email, which is then used to
     * load the matching contact. Mirrors Laravel:
     * <pre>
     *   DB::table('powerapp_contacts')-&gt;where('email', $email)-&gt;latest('id')-&gt;first();
     * </pre>
     */
    Optional<PowerAppContacts> findFirstByEmailIgnoreCaseOrderByIdDesc(String email);

    /**
     * Update the {@code b2c_status} (and optional {@code error_message}) after the
     * onboarding email send completes. Matches Laravel's:
     * <pre>
     *   DB::table('powerapp_contacts')
     *     -&gt;where('powerapp_contact_id', $id)
     *     -&gt;update(['b2c_status' =&gt; 'MAIL_SENT' | 'MAIL_FAILED', 'error_message' =&gt; $msg]);
     * </pre>
     */
    @Modifying
    @Transactional
    @Query("UPDATE PowerAppContacts p SET p.b2cStatus = :status, p.errorMessage = :errorMessage "
            + "WHERE p.powerAppContactId = :contactId")
    int updateB2cStatus(@Param("contactId") String contactId,
                        @Param("status") String status,
                        @Param("errorMessage") String errorMessage);
}
