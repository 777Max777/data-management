export interface BaseTree {
    parentId?: number | null;
    label: string;
    items?: BaseTree[];
}

export interface Base {
    id?: number;
    name: string;
    parent?: Base;
}

export interface Case extends BaseTree, Base {
    id?: number;
    name: string;
    description?: string;
}