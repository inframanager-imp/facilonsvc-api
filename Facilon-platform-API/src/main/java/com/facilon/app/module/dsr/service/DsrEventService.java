package com.facilon.app.module.dsr.service;

import com.facilon.app.module.dsr.dto.DsrCaseEventDto;
import com.facilon.app.module.dsr.model.DsrCase;
import com.facilon.app.module.dsr.model.DsrCaseEvent;
import com.facilon.app.module.dsr.repository.DsrCaseEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/** Records and reads DSR timeline/audit events. */
@Service
@RequiredArgsConstructor
public class DsrEventService {

    private final DsrCaseEventRepository eventRepository;
    private final DsrStatusMapper statusMapper;

    public void record(DsrCase dsrCase,
                       String eventCode,
                       DsrCase.CaseStatus fromStatus,
                       DsrCase.CaseStatus toStatus,
                       String note,
                       String actor,
                       String actorRole,
                       boolean investorVisible) {
        DsrCaseEvent event = DsrCaseEvent.builder()
                .dsrCaseId(dsrCase.getId())
                .caseId(dsrCase.getCaseId())
                .eventCode(eventCode)
                .actor(actor)
                .actorRole(actorRole)
                .fromStatus(fromStatus != null ? fromStatus.name() : null)
                .toStatus(toStatus != null ? toStatus.name() : null)
                .note(note)
                .investorVisible(investorVisible)
                .build();
        eventRepository.save(event);
    }

    public List<DsrCaseEventDto> investorTimeline(Long dsrCaseId) {
        return eventRepository.findByDsrCaseIdAndInvestorVisibleTrueOrderByCreatedAtAsc(dsrCaseId)
                .stream().map(this::toDto).toList();
    }

    public List<DsrCaseEventDto> fullTimeline(Long dsrCaseId) {
        return eventRepository.findByDsrCaseIdOrderByCreatedAtAsc(dsrCaseId)
                .stream().map(this::toDto).toList();
    }

    private DsrCaseEventDto toDto(DsrCaseEvent e) {
        DsrCase.CaseStatus toStatus = null;
        if (e.getToStatus() != null) {
            try {
                toStatus = DsrCase.CaseStatus.valueOf(e.getToStatus());
            } catch (IllegalArgumentException ignored) {
                // event code may not be a status (e.g. file uploads) - leave title as code
            }
        }
        String title = toStatus != null ? statusMapper.timelineTitle(toStatus) : e.getEventCode();
        return DsrCaseEventDto.builder()
                .eventCode(e.getEventCode())
                .title(title)
                .note(e.getNote())
                .actor(e.getActor())
                .actorRole(e.getActorRole())
                .fromStatus(e.getFromStatus())
                .toStatus(e.getToStatus())
                .investorVisible(e.isInvestorVisible())
                .createdAt(e.getCreatedAt() != null
                        ? e.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }
}
