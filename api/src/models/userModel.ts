export type User = {
    id: number;
    username: string;
    passhash: string;
};

export type UserRequest = {
    username: string;
    password: string;
};