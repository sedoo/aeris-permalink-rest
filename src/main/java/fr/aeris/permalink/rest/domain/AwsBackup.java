package fr.aeris.permalink.rest.domain;

import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.aeris.permalink.rest.config.AwsConfig;
import fr.aeris.permalink.rest.dao.PermalinkDao;

@Component
public class AwsBackup {

	private static final String DATE_FORMAT = "yyyy_dd_MM";

	@Autowired
	PermalinkDao permalinkDao;

	@Autowired
	AwsConfig awsConfig;

	private Logger logger = LoggerFactory.getLogger(AwsBackup.class);
	private int CONSERVATION_DURATION_DAY = 30;

	@PostConstruct
	public void init() {
		execute();
	}

	public void  execute() {

		File backupFile = null;

		try {
			List<Permalink> all = permalinkDao.findAll();
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(all);
			backupFile =File.createTempFile("permalinkBackup", ".json");
			FileUtils.writeStringToFile(backupFile, json, Charset.defaultCharset());

			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			String backupKey = "BACKUP_"+sdf.format(new Date())+".json";

			AmazonS3 s3client = AmazonS3ClientBuilder
					.standard()
					.withCredentials(new AWSStaticCredentialsProvider(awsConfig.getCredentials()))
					.withRegion(Regions.EU_CENTRAL_1)
					.build();


			if(!s3client.doesBucketExist(awsConfig.getBucketName())) {
				s3client.createBucket(awsConfig.getBucketName());
			}

			s3client.putObject(
					awsConfig.getBucketName(), 
					backupKey, backupFile
					);
			
			cleanOldBackups();


		}
		catch (Exception e) {
			logger.error("An error has occured while backuping: "+e.getMessage());
		}
		finally {
			if (backupFile != null) {
				backupFile.delete();
			}
		}
	}

	private void cleanOldBackups() {
		AmazonS3 s3client = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsConfig.getCredentials()))
				.withRegion(Regions.EU_CENTRAL_1)
				.build();
		
		ObjectListing objectListing = s3client.listObjects(awsConfig.getBucketName());
		for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
		    if (isTooOld(os)) {
		    	s3client.deleteObject(awsConfig.getBucketName(),os.getKey());
		    }
		}
		
	}

	private boolean isTooOld(S3ObjectSummary os) {
		String aux = os.getKey();
		aux= aux.substring(0, aux.lastIndexOf('.'));
		String[] split = aux.split("_");
		aux =split[1]+"_"+split[2]+"_"+split[3];
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date date = null;
		try {
			date = sdf.parse(aux);
			
		} catch (ParseException e) {
			// functionally impossible case
		}
		Date current = new Date();
		long duration = ChronoUnit.DAYS.between(date.toInstant(),current.toInstant());
		if (duration > CONSERVATION_DURATION_DAY) {
			return true;
		}
		return false;
	}

	@Scheduled(cron="0 0 1 * * ?")
	public void dailyBackup() {
		execute();
	}

}
