package com.PT.demo;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PersonaIntegrationTest{
	
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private Firestore firestore;

	private CollectionReference collectionReference;
	private DocumentReference documentReference;
	private ApiFuture<QuerySnapshot> querySnapshotFuture;
	private QuerySnapshot querySnapshot;

	@SuppressWarnings({ "unchecked", "null" })
	@BeforeEach
	void setUp(){
		collectionReference = Mockito.mock(CollectionReference.class);
		documentReference = Mockito.mock(DocumentReference.class);
		querySnapshotFuture = Mockito.mock(ApiFuture.class);
		querySnapshot = Mockito.mock(QuerySnapshot.class);
		when(firestore.collection(anyString())).thenReturn(collectionReference);
		when(collectionReference.document()).thenReturn(documentReference);
		when(collectionReference.document(anyString())).thenReturn(documentReference);
		when(collectionReference.get()).thenReturn(querySnapshotFuture);
		when(collectionReference.whereEqualTo(anyString(), anyString())).thenReturn(Mockito.mock(Query.class));

		when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());
		try {
			when(querySnapshotFuture.get()).thenReturn(querySnapshot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Pruebas de tipo @ParameterizesTest
		@Test
		@DisplayName("")
		// Pruebas de tipo @RepeatedTest
		// Pruebas de tipo @Test
	}
}
