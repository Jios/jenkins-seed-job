### Fields
```
# comment starts with #

---							# for yaml file: https://en.wikipedia.org/wiki/YAML

# project info, http://stash.tutk.com:7990/projects
key: 						# stash project key--look for the Key column
name: 						# stash project name--look for the Project column

# repositories
repos:
  # first repository
  - name: 					# stash repository name, e.g. look for Name column from http://stash.tutk.com:7990/projects/ABS
    label: 					# jenkins slave label: android, ios, linux, ...
    build_command: 			# terminal command, e.g. fastlane ios build
    # environment for SRVM and Jenkins
    environment:
      SRVM_CUSTOMER_IDS: 	# SRVM client ID
      SRVM_RELEASE_FOR: 	# just put pm
      SRVM_RELEASE_BY: 		# developer username, e.g. jian_li
      SRVM_PRODUCT_CATALOG: # SRVM product catalog, e.g. kalay cam
      BUILD_PLATFORM: 		# SRVM platform, e.g. android or ios
      BUILD_OUTPUT_PATH: 	# build output directory, e.g. output
      REPORT_PATH: 			# report output directory, e.g. reports
    email_list:
      - jian_li@tutk.com 	# email list...put as many as necessary
      - stanley_huang@tutk.com
  
  # second repository
  - name:
    ...
```

### Template
```
---

key: 
name: 

repos:
  - name: 
    label: 
    build_command: 
    environment:
      SRVM_CUSTOMER_IDS: 
      SRVM_RELEASE_FOR: 
      SRVM_RELEASE_BY: 
      SRVM_PRODUCT_CATALOG: 
      BUILD_PLATFORM: 
      BUILD_OUTPUT_PATH: 
      REPORT_PATH: 
    email_list:
      - 
```