package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorInvitationRepository extends JpaRepository<InvestorInvitation, Long> {

    List<InvestorInvitation> findBySenderId(Long senderId);

    Optional<InvestorInvitation> findByInvitationToken(String invitationToken);

    Optional<InvestorInvitation> findByRecipientEmailAndInvitationType(String recipientEmail, String invitationType);
}
