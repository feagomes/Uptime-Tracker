import axios from "axios";

const API = axios.create({ baseURL: "http://localhost:8080/api/services" });

export const getServices = () => API.get("");
export const createService = (data) => API.post("", data);
export const getServiceHistory = (id) => API.get(`/${id}/history`);
export const getServiceStats = (id) => API.get(`/${id}/stats`);
