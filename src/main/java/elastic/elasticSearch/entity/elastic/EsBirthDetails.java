package elastic.elasticSearch.entity.elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import elastic.elasticSearch.helper.Indices;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@Document(indexName = Indices.index_name)
public class EsBirthDetails {

    @Id
    private int id;

    @Field(name = "full_name")
    private String fullName;

    @Field(name = "date_of_birth")
    private Instant dateOfBirth;

    @Field(name = "gender")
    private String gender;

    @Field(name = "nic_number")
    private Integer nicNumber;

    @Field(name = "update_at")
    private Instant updateAt;
}

