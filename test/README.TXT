##########################################################################
#
#	RTestingTools (2013)
#	
#	1. Directory hierarchy
#	2. Config file
#	3. Scenario
#	
##########################################################################


###############################
#                             #
# 1. Directory hierarchy      #
#                             #
###############################
-RtestingTools
			|
			-lib/
			|
			-LOG/
			|
			-SCENARIO/
			|
			--config.txt
			|
			--RTestingTools.jar
			|
			--README.txt
                        |
                        --template.xlsx

* LOG : rtestingtools scenario output files
* SCENARIO : rtestingtools scenario files



#####################
#		    #
# 2. Config file    #
#		    #
#####################

Remote server and port
=======================
# remote server server-1,server-2,...,server-n
remote_server $(server_ip-1):$(server_port-1),...,$(server_ip-n),$(server_port-n)

Offline file port and SSH Connection
====================================
#offline_file server-1
offline_file $(server_ip-1),$(server_user_id),$(server_password)

Server id
==========
# id server ocs
server_id $(server_id)

Local hostname
==============
# hostname identification to server
local_host   $(hostname)

# Scenario
===========
# Scenario file name
# Put scenario file in SCENARIO directory without *.txt extension
scenario $(filename)



##################
#		 #
# 3. SCENARIO	 #
#		 #
##################


Pada scenario, $(id) hanya sebagai identifier suatu test case agar mudah
diidentifikasi saat membaca output.

Namun, apabila diawali dengan simbol '%' (tanpa tanda kutip), akan dijadikan
sebagai session id


Check balance,bonus,last transaction
====================================
input:
$(id) {887 | 888 | 889} $(remote_server_id),$(A#),$(cell-info),$(bonus-type),<$(timestamp)>


Voice init
==========
input:
$(id) {voice_init | voice_int_init} $(remote_server_id),$(A#),$(B#),$(cell-info),$( {3GPP-MS-Timezone | Access-Code} ), <$(timestamp)>


Voice intermediate
==================
input:
$(id) {voice_intermediate | voice_int_intermediate} $(remote_server_id),$(A#),$(B#),$(cell-info),$( {3GPP-MS-Timezone | Access-Code} ),$(CC-Request-Number),$(duration),<$(timestamp)>


Voice terminate
===============
input:
$(id) {voice_terminate | voice_int_terminate} $(remote_server_id),$(A#),$(B#),$(cell-info),$( {3GPP-MS-Timezone | Access-Code} ),$(CC-Request-Number),$(duration),<$(timestamp)>


Voice (init->terminate)
=======================
input:
$(id) {voice | voice_int} $(remote_server_id),$(A#),$(B#),$(cell-info),$( {3GPP-MS-Timezone | Access-Code} ),$(duration),<$(timestamp)>


Voice roaming init
==================
input:
$(id) voice_roaming_init $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),<$(timestamp)>


Voice roaming intermediate
==========================
input:
$(id) voice_roaming_intermediate $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),$(CC-Request-Number),$(duration),<$(timestamp)>


Voice roaming terminate
=======================
input:
$(id) voice_roaming_terminate $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),$(CC-Request-Number),$(duration),<$(timestamp)>


Voice roaming (init->terminate)
===============================
input:
$(id) voice_roaming $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),$(duration),<$(timestamp)>


Voice roaming MTC init
======================
input:
$(id) voice_mtc_init $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),<$(timestamp)>


Voice roaming MTC terminate
===========================
input:
$(id) voice_mtc_terminate $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),$(CC-Request-Number),$(duration),<$(timestamp)>


Voice roaming MTC (init->terminate)
===================================
input:
$(id) voice_mtc $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),$(duration),<$(timestamp)>


Voice UCB init
==================
input:
$(id) voice_ucb_init $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),<$(timestamp)>


Voice UCB terminate
=======================
input:
$(id) voice_ucb_terminate $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),$(CC-Request-Number),$(duration),<$(timestamp)>


Voice UCB (init->terminate)
===============================
input:
$(id) voice_ucb $(remote_server_id),$(A#),$(B#),$(cell-info),$(Access-Code),$(VLR-id),$(duration),<$(timestamp)>

Video call init
===============
$(id) {video_init | video_init_int} $(remote_server_id),$(A#),$(B#),$(cell-info),$(3GPP-MS-Timezone),$(VLR-id),<$(timestamp)>


Video call terminate
====================
input:
$(id) {video_terminate | video_terminate_int} $(remote_server_id),$(A#),$(B#),$(cell-info),$(3GPP-MS-Timezone),$(VLR-id),$(duration),<$(timestamp)>


Video call (init->terminate)
============================
input:
$(id) {video | video_int} $(remote_server_id),$(A#),$(B#),$(cell-info),$(3GPP-MS-Timezone),$(VLR-id),$(duration),<$(timestamp)>


Sms init
========
$(id) sms_init $(remote_server_id),$(A#),$(B#),$(cell-info),<$(timestamp)>


Sms terminate
=============
$(id) sms_terminate $(remote_server_id),$(A#),$(B#),$(cell-info),$(CC-service-specific-units),<$(timestamp)>


Sms (init->terminate)
=====================
$(id) sms $(remote_server_id),$(A#),$(B#),$(cell-info),$(CC-service-specific-units),<$(timestamp)>


Sms Roaming(init->terminate)
=====================
$(id) sms_roaming $(remote_server_id),$(A#),$(B#),$(cell-info),$(VLR-id),$(CC-service-specific-units),<$(timestamp)>

Gprs init
=========
$(id) gprs_init $(remote_server_id),$(A#),$(Called-Station-ID),$(3GPP-SGSN-MCC-MNC),$(Rating-Group),<$(timestamp)>

Gprs intermediate
=================
$(id) gprs_intermediate $(remote_server_id),$(A#),$(Called-Station-ID),$(3GPP-SGSN-MCC-MNC),$(CC-Request-Number),$(Rating-Group),$(CC-Total-Octets),<$(timestamp)>


Gprs terminate
==============
$(id) gprs_terminate $(remote_server_id),$(A#),$(Called-Station-ID),$(3GPP-SGSN-MCC-MNC),$(CC-Request-Number),$(Rating-Group),$(CC-Total-Octets),<$(timestamp)>


Gprs (init->terminate)
======================
$(id) gprs $(remote_server_id),$(A#),$(Called-Station-ID),$(3GPP-SGSN-MCC-MNC),$(Rating-Group),$(CC-Total-Octets),<$(timestamp)>


Mms
===
input:
$(id) mms $(remote_server_id),$(A#),$(Service-Unit),$(Modifier 5),<$(timestamp)>


Direct Debit
============
input:
$(id) direct_debit $(remote_server_id),$(A#),$(Cost-Code),<$(timestamp)>


TalkMania activation
====================
input:
$(id) talkmania $(remote_server_id),$(A#),$(Cost-Code),<$(timestamp)>


First Location Update
=====================
input:
$(id) flu $(remote_server_id),$(A#),$(cell-info),<$(timestamp)>


Subscriber status check
=======================
input:
$(id) cek_status $(database_url),$(database_username),$(database_password),$(A#)


Offline event check
=======================
input:
$(id) offline_transaction $(ocs_ip),$(ocs_username),$(ocs_password),$(A#),$(event_id)


Vertical space
==============
input:
space


