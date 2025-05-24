package elastic.elasticSearch.entity.elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import elastic.elasticSearch.helper.Indices;
import lombok.Data;
import java.time.Instant;

@Data
@Document(indexName = Indices.Birth_index)
public class EsBirthDetails {

    @Id
    private int id;

    private String full_name;

    private Instant date_of_birth;

    private String gender;

    private Integer nic_number;

    private Instant update_at;
}

