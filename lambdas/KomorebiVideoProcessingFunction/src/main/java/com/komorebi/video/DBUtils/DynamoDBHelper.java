package com.komorebi.video.DBUtils;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;


public class DynamoDBHelper {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Recording> recordingTable;

    public DynamoDBHelper() throws Exception{
        try{
            DynamoDbClient dbc = DynamoDbClient.create();
            this.enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(dbc)
                    .build();
            TableSchema<Recording> recordingSchema = TableSchema.fromBean(Recording.class);
            this.recordingTable = this.enhancedClient.table("Recording", recordingSchema);
        } catch (DynamoDbException e) {
            throw new Exception("Impossible to create DynamoDB client", e);
        }
    }

    public Recording getRecording(String id) throws Exception{
        if (this.enhancedClient == null){
            throw new Exception("DynamoDBClient not initialized");
        }
        try{
            Key key = Key.builder().partitionValue(id).build();
            Recording recording = this.recordingTable.getItem(r->r.key(key));
            if (recording == null){
                throw new Exception("Recording not found in the DB");
            }
            return recording;
        } catch (DynamoDbException e){
            throw new Exception("Unknown exception while fetching the recording from DB");
        }
    }

    public void setRecordingAsProcessed(Recording recording) throws Exception{
        if (enhancedClient == null){
            throw new Exception("DynamoDBClient not initialized");
        }
        try {
            recording.setIsProcessed(true);
            this.recordingTable.updateItem(r->r.item(recording));
        } catch (DynamoDbException e){
            throw new Exception("Unknown error while setting the recording as processed in the DB");
        }
    }
}
