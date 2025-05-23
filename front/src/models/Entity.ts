import { Field } from "./Field";

export interface Entity {
  id: number;
  name: string;
  children?: Entity[];
  description?: string;
  fields?: Field[];
  parent?: {
    id: number;
  };
}

export interface TreeData extends Entity {
  parentId?: number | null;
  label: string;
  items?: TreeData[];
}
