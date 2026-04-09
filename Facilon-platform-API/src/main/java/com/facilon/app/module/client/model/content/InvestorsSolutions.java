package com.facilon.app.module.client.model.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "investors_solutions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorsSolutions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "title")
    private String title;

    @Column(name = "image_upload")
    private String imageUpload;

    @Column(name = "slug_url")
    private String slugUrl;

    @Column(name = "short_desc", columnDefinition = "TEXT")
    private String shortDesc;

    @Column(name = "long_desc", columnDefinition = "TEXT")
    private String longDesc;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;
}
