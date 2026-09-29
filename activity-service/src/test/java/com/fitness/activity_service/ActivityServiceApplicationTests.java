package com.fitness.activity_service;

import com.fitness.activity_service.domain.models.Activity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.bson.Document;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ActivityServiceApplicationTests {
	@Autowired
	private MappingMongoConverter mongoConverter;

	@Test
	void contextLoads() {
	}

	@Test
	void activityDocumentsDoNotContainClassMetadata() {
		Activity activity = Activity.builder().userId("user-1").build();
		Document document = new Document();
		mongoConverter.write(activity, document);

		assertThat(document).doesNotContainKey("_class");
		assertThat(mongoConverter.read(Activity.class, document).getUserId()).isEqualTo("user-1");
	}

}
