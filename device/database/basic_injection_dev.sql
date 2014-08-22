

insert into sgvs (device_id, datetime_recorded, sgv, transfered) 
values (584923, now(), 99, 'no');

use cloop;
select * from courses;

show tables;
select * from iob;
select * from injections;
insert into injections (units_intended, units_delivered, datetime_intended, datetime_delivered, iob_unitsalerts, carbs, carb_sensitivity, units_for_carbs,
	bg_current, units_for_bg, current_basal, status, transferred)
values (10, 10, now(), now(), 0, 50, 10, 5, 
	140, 4, 2, 'successful', 'no');

insert into iob (iob_datetime, iob) values 
	((select datetime_delivered from injections where injection_id = 1)+ interval 5 minute, 
		ifnull((select units_delivered * (select iob_dist_pct from iob_dist where iob_dist.interval = 5) / 100 from injections where injection_id = 1),0))
	on duplicate key update iob = iob + ifnull((select units_delivered * (select iob_dist_pct from iob_dist where iob_dist.interval = 5) / 100 from injections where injection_id = 1),0);
insert into iob (datetime_iob, iob) values
    ((select datetime_delivered from injections where injection_id = 23)+ interval 0 minute,
		ifnull((select units_delivered * (select iob_dist_pct from iob_dist where iob_dist.interval = 0) / 100 from injections where injection_id = 23),0))
    on duplicate key update iob = iob + ifnull((select units_delivered * (select iob_dist_pct from iob_dist where iob_dist.interval = 0) / 100 from injections where injection_id = 23),0);
insert into iob (datetime_iob, iob) values
	((select datetime_delivered from injections where injection_id = 24)+ interval 0 minute,
		ifnull((select units_delivered * (select iob_dist_pct from iob_dist where iob_dist.interval = 0) / 100 from injections where injection_id = 24),0))
	on duplicate key update iob = iob + ifnull((select units_delivered * (select iob_dist_pct from iob_dist where iob_dist.interval = 0) / 100 from injections where injection_id = 24),0);

insert into iob_dist (iob_dist.interval, iob_dist_pct) values (5, 100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (0, 100);
select * from iob;
select count(*) from iob;
delete from iob where iob = 1000;

insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), "yes");
delete from courses_to_injections;
delete from injections;
delete from iob;
delete from courses;
delete from sgvs;
delete from alerts;
delete from automode_switch;
delete from logs;
delete from sgvs;
insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150);
insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute);
select * from sgvs;
select * from courses;
select * from injections;
select * from iob_dist;
select * from iob;
select * from logs;
select * from alerts;
select * from courses_to_injections;
select * from automode_switch;
select * from halts;


insert into injections (injection_type,                     
units_intended, units_delivered, temp_rate, datetime_intended,                     
cur_iob_units, cur_bg_units, cur_bg, correction_units,                     
carbs_to_cover, carbs_units,                     
cur_basal_units, all_meal_carbs_absorbed,                     
status, transferred) values 
( 'bolus',
6.92,6.92,null,now(),
2.0,null,None,0,90,6.92307692308,0.55,'True','initial','awaiting completion')

select concat('Injected 6.92u ',(select cast(ifnull(carbs_to_cover,0) as char) from injections where injection_id = 12),'carbs ',(select cast(ifnull(cur_bg,0) as char) from injections where injection_id = 12),'bg')
insert into alerts (datetime_recorded, datetime_to_alert, src, code, type, title, message, transferred) values (now(), '2014-08-17 12:49:05.039000','device','process_injection','info','Injected 6.92u '+(select concat(carbs_to_cover) from injections where injection_id = 12)+'carbs '+(select concat(ifnull(cur_bg,0)) from injections where injection_id = 12)+'bg','Injection #12 of 6.92 units was given at 2014-08-17 12:49:05.039000','no')
insert into alerts (datetime_recorded, datetime_to_alert, src, code, type, title, message, transferred) values (now(), '2014-08-17 12:49:05.039000','device','process_injection','info','Injected 6.92u bg','Injection #12 of 6.92 units was given at 2014-08-17 12:49:05.039000','no')


08-17 16:19:02.349: E/AndroidRuntime(8812): android.database.sqlite.SQLiteException: 10 values for 11 columns (code 1): , while compiling: 
INSERT OR REPLACE INTO alerts (alert_id,datetime_recorded,datetime_to_alert,src,code,type,title,message,value,option1,option2) 
values (163,'2014-08-17T16:18:37','2014-08-17T16:18:08','device','process_injection','info','Injection #98 of 3.31 units was given at 2014-08-17 16:18:08.452801','null','null','null')
insert into alerts (datetime_recorded,datetime_to_alert,src,code,type,title,message,value) values (now(), now(), 'test', 'mysql','Test','Test Alert','Some nice message','value')
