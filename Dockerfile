FROM gabrigiunchi/maven-gcloud
COPY . .
CMD ["./deploy.sh"]