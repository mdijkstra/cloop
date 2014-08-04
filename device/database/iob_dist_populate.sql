/****************************************************************************************
*
* Edward Robinson
* 
* This file populates the insulin on board distribution based on calculations from
* the excel file.
*
****************************************************************************************/

truncate table iob_dist;
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (0, 100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (5,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (10,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (15,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (20,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (25,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (30,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (35,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (40,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (45,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (50,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (55,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (60,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (65,96.557918595679);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (70,92.9081790123457);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (75,89.06640625);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (80,85.0493827160494);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (85,80.8750482253086);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (90,76.5625);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (95,72.1319926697531);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (100,67.6049382716049);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (105,63.00390625);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (110,58.3526234567901);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (115,53.6759741512346);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (120,49);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (125,44.3519000771605);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (130,39.7600308641975);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (135,35.25390625);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (140,30.8641975308642);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (145,26.6227334104938);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (150,22.5625);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (155,18.7176408179012);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (160,15.1234567901235);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (165,11.81640625);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (170,8.8341049382716);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (175,6.21532600308643);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (180,4);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (185,2.22921489197531);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (190,0.945216049382718);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (195,0.19140625);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (200,0.012345679012346);
select * from iob_dist;