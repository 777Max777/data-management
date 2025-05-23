import { Entity } from "./Entity";

export interface Field {
  id?: string;
  name: string;
  fieldType: Type;
  entities?: Entity[];
  group?: any;
  relEntity?: Entity;
}

export enum Type {
  STRING = "STRING",
  NUMBER = "NUMBER",
  BOOLEAN = "BOOLEAN",
  RELATION = "RELATION",
  MULTIPLE_RELATION = "MULTIPLE_RELATION"
}

