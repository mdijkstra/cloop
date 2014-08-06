/****************************************************************************************
*
* Edward Robinson
* 
* This file populates the insulin on board distribution based on calculations from
* the excel file.
*
****************************************************************************************/

truncate table iob_dist;
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (0,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (5,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (10,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (15,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (20,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (25,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (30,100);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (35,99.8565883517041);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (40,99.4271760828512);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (45,98.7142265022923);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (50,97.7218294210072);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (55,96.4556776910438);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (60,94.9230345486654);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (65,93.1326919490439);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (70,91.0949201315085);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (75,88.8214087046672);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (80,86.3251995893658);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (85,83.6206122041528);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (90,80.7231613224234);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (95,77.6494680724496);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (100,74.4171645908448);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (105,71.0447928764127);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (110,67.5516984246025);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (115,63.9579192527338);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (120,60.2840709525924);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (125,56.551228429791);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (130,52.7808050082903);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (135,48.9944295935966);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (140,45.2138225992812);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (145,41.4606713485688);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (150,37.7565056657461);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (155,34.1225743710584);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (160,30.5797233875763);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (165,27.1482761592683);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (170,23.8479170662562);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (175,20.6975785060402);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (180,17.7153322884444);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (185,14.9182859672966);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (190,12.3224847035278);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (195,9.94281922265331);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (200,7.79294039463344);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (205,5.88518092612266);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (210,4.23048461431821);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (215,2.8383435682403);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (220,1.71674375757206);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (225,0.872119201416532);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (230,0.309315059765303);
insert into iob_dist (iob_dist.interval, iob_dist_pct) values (235,0.0315598394032557);
select * from iob_dist;