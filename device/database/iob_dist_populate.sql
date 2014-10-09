/****************************************************************************************
*
* Edward Robinson
* 
* This file populates the insulin on board distribution based on calculations from
* the excel file.
*
****************************************************************************************/

truncate table iob_dist;
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (0,100,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (5,100,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (10,100,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (15,100,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (20,100,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (25,100,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (30,100,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (35,99.826489335028,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (40,99.3071615781462,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (45,98.4456210855322,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (50,97.2478473157369,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (55,95.7221533296915,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (60,93.8791280945186,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (65,91.7315629915828,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (70,89.2943630388474,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (75,86.584443443691,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (80,83.6206122041528,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (85,80.4234395734022,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (90,77.015115293407,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (95,73.4192945886575,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (100,69.6609339888179,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (105,65.7661181197634,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (110,61.7618786651495,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (115,57.6760067549951,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (120,53.5368600833851,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (125,49.373166093976,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (130,45.2138225992812,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (135,41.0876972175254,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (140,37.0234270190583,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (145,33.0492197728705,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (150,29.1926581726429,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (155,25.4805084010858,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (160,21.9385343612129,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (165,18.591318863863,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (170,15.4620930125062,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (175,12.572574969475,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (180,9.94281922265331,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (185,7.5910773987749,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (190,5.53367158934793,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (195,3.78488106837682,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (200,2.3568431881092,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (205,1.2594691406335,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (210,0.500375169977729,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (215,0.0848297121268715,'bolus');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (220,0.0157168298296539,'bolus');

insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (0,100,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (5,100,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (10,100,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (15,100,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (20,100,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (25,100,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (30,100,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (35,99.8565883517041,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (40,99.4271760828512,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (45,98.7142265022923,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (50,97.7218294210072,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (55,96.4556776910438,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (60,94.9230345486654,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (65,93.1326919490439,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (70,91.0949201315085,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (75,88.8214087046672,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (80,86.3251995893658,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (85,83.6206122041528,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (90,80.7231613224234,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (95,77.6494680724496,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (100,74.4171645908448,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (105,71.0447928764127,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (110,67.5516984246025,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (115,63.9579192527338,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (120,60.2840709525924,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (125,56.551228429791,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (130,52.7808050082903,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (135,48.9944295935966,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (140,45.2138225992812,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (145,41.4606713485688,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (150,37.7565056657461,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (155,34.1225743710584,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (160,30.5797233875763,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (165,27.1482761592683,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (170,23.8479170662562,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (175,20.6975785060402,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (180,17.7153322884444,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (185,14.9182859672966,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (190,12.3224847035278,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (195,9.94281922265331,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (200,7.79294039463344,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (205,5.88518092612266,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (210,4.23048461431821,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (215,2.8383435682403,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (220,1.71674375757206,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (225,0.872119201416532,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (230,0.309315059765303,'square');
insert into iob_dist (iob_dist.interval, iob_dist_pct, injection_type) values (235,0.0315598394032557,'square');
