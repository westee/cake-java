create table USER_ROLE
(
    USER_ID BIGINT not null,
    ROLE_ID BIGINT not null,
    constraint ROLE_ID
        foreign key (ROLE_ID) references ROLE (ID),
    constraint USER_ID
        foreign key (USER_ID) references USER (ID)
);
