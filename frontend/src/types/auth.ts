export interface LoginRequest {
    email: string;
    password: string;
}


export interface LoginResponse {
    token: string;
}

export interface RegisterRequest {
    name: string;
    email: string;
    password: string;
}

//(Ajuste as propriedades caso seu backend retorne algo específico além do status 201)
export interface RegisterResponse {
    id: number;
    name: string;
    email: string;
}