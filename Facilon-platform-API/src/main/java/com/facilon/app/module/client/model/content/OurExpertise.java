package com.facilon.app.module.client.model.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Company expertise showcase content.
 * Global CMS content.
 */
@Entity
@Table(name = "our_expertise")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OurExpertise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_upload")
    private String imageUpload;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;
}
