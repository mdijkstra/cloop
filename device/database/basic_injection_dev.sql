

insert into sgvs (device_id, datetime_recorded, sgv, transfered) 
values (584923, now(), 99, 'no');

use cloop;
select * from courses;

show tables;
select * from iob;
select * from injections;
insert into injections (units_intended, units_delivered, datetime_intended, datetime_delivered, iob, carbs, carb_sensitivity, units_for_carbs,
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


delete from courses_to_injections;
delete from injections;
delete from iob;
delete from courses;
delete from sgvs;
insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150);
insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute);
select * from sgvs;
select * from courses;
select * from injections;
select * from iob_dist;
select * from iob;
select * from alerts;
select * from courses_to_injections;

insert into injections (
                    units_intended, units_delivered, datetime_intended,
                     cur_iob_units, cur_bg_units, correction_units,
                     carbs_to_cover, carbs_units,
                     cur_basal_units, all_meal_carbs_absorbed,
                     status, transferred) values ( 4.30769230769,4.30769230769,now(),0,2.0,2.0,30,2.30769230769,0.55,'True','initial','awaiting completion');


select datetime(round(UNIX_TIMESTAMP(datetime_iob) / 300)*300) as interval5, sum(iob) from iob group by round(UNIX_TIMESTAMP(datetime_iob) / 300)
