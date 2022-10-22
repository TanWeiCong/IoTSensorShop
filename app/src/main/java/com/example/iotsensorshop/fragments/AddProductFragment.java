package com.example.iotsensorshop.fragments;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.activities.MainActivity;
import com.example.iotsensorshop.activities.NewProductActivity;
import com.example.iotsensorshop.activities.PopularProductsActivity;
import com.example.iotsensorshop.activities.ShowAllActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AddProductFragment extends Fragment {

    FirebaseFirestore firestore;
    ImageView imgProduct;
    EditText name, description, price, type, stock;
    Button addProduct;
    String choices;
    Uri path;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        imgProduct = view.findViewById(R.id.img_product);
        name = view.findViewById(R.id.product_name);
        description = view.findViewById(R.id.product_description);
        price = view.findViewById(R.id.product_price);
        type = view.findViewById(R.id.product_type);
        addProduct = view.findViewById(R.id.btn_add_product);
        stock = view.findViewById(R.id.product_stock);

        firestore = FirebaseFirestore.getInstance();

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = name.getText().toString().trim();
                String productDescription = description.getText().toString().trim();
                String productPrice = price.getText().toString().trim();
                String productType = type.getText().toString().trim();
                String productStock = stock.getText().toString().trim();

                if(TextUtils.isEmpty(productName)){
                    Toast.makeText(getContext(), "Please enter product name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productDescription)){
                    Toast.makeText(getContext(), "Please enter product description!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productPrice)){
                    Toast.makeText(getContext(), "Please enter product price!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productType)){
                    Toast.makeText(getContext(), "Please product type!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productStock)){
                    Toast.makeText(getContext(), "Please enter product stock!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String documentId = productName + productType;

                int integerPrice = Integer.valueOf(productPrice);
                int integerStock = Integer.valueOf(productStock);

                DocumentReference documentReference = firestore.collection("ShowAll").document(documentId);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                          if (task.isSuccessful()) {
                              DocumentSnapshot document = task.getResult();
                              if (document.exists()) {
                                  Toast.makeText(getActivity(), "The product already exists! Do you want to add the product to other category?", Toast.LENGTH_SHORT).show();
                                  CharSequence options[] = new CharSequence[]
                                          {
                                                  "New Products",
                                                  "Popular Products",
                                                  "Cancel"
                                          };

                                  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                  builder.setTitle("Do you want to add product to other categories?");

                                  builder.setItems(options, new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          if (which == 0) {
                                              DocumentReference documentReference = firestore.collection("NewProducts").document(documentId);
                                              documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                      if (task.isSuccessful()) {
                                                          DocumentSnapshot document = task.getResult();
                                                          if (document.exists()) {
                                                              Toast.makeText(getActivity(), "The product already exists!", Toast.LENGTH_SHORT).show();
                                                          } else {
                                                              Map<String, Object> product = new HashMap<>();
                                                              product.put("name", productName);
                                                              product.put("type", productType);
                                                              product.put("description", productDescription);
                                                              product.put("price", integerPrice);
                                                              product.put("img_url", path);
                                                              product.put("documentId", documentId);
                                                              product.put("stock", integerStock);
                                                              documentReference.set(product);

                                                              Toast.makeText(getActivity(), "The product is added!", Toast.LENGTH_SHORT).show();
                                                              Intent intent = new Intent(getActivity().getApplication(), MainActivity.class);
                                                              startActivity(intent);
                                                          }
                                                      } else {
                                                          Toast.makeText(getActivity(), "Failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              });

                                              storageReference = FirebaseStorage.getInstance().getReference("images/*");

                                              storageReference.putFile(path)
                                                      .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                          @Override
                                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                              Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                                          }
                                                      }).addOnFailureListener(new OnFailureListener() {
                                                  @Override
                                                  public void onFailure(@NonNull Exception e) {
                                                      if (path == null) {
                                                          Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              });
                                          }

                                          if (which == 1) {
                                              DocumentReference documentReference = firestore.collection("PopularProducts").document(documentId);
                                              documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                      if (task.isSuccessful()) {
                                                          DocumentSnapshot document = task.getResult();
                                                          if (document.exists()) {
                                                              Toast.makeText(getActivity(), "The product already exists!", Toast.LENGTH_SHORT).show();
                                                          } else {
                                                              Map<String, Object> product = new HashMap<>();
                                                              product.put("name", productName);
                                                              product.put("type", productType);
                                                              product.put("description", productDescription);
                                                              product.put("price", integerPrice);
                                                              product.put("img_url", path);
                                                              product.put("documentId", documentId);
                                                              product.put("stock", integerStock);
                                                              documentReference.set(product);

                                                              Toast.makeText(getActivity(), "The product is added!", Toast.LENGTH_SHORT).show();
                                                              Intent intent = new Intent(getActivity().getApplication(), MainActivity.class);
                                                              startActivity(intent);
                                                          }
                                                      } else {
                                                          Toast.makeText(getActivity(), "Failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              });

                                              storageReference = FirebaseStorage.getInstance().getReference("images/*");

                                              storageReference.putFile(path)
                                                      .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                          @Override
                                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                              Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                                          }
                                                      }).addOnFailureListener(new OnFailureListener() {
                                                  @Override
                                                  public void onFailure(@NonNull Exception e) {
                                                      if (path == null) {
                                                          Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              });
                                          }

                                          if (which == 2){
                                              Toast.makeText(getActivity(), "The product already exists!", Toast.LENGTH_SHORT).show();
                                              Intent intent = new Intent(getActivity().getApplication(), MainActivity.class);
                                              startActivity(intent);
                                              dialog.dismiss();
                                          }
                                      }
                                  });
                                  builder.show();
                              }
                              else
                              {
                                  Map<String, Object> product = new HashMap<>();
                                  product.put("name", productName);
                                  product.put("type", productType);
                                  product.put("description", productDescription);
                                  product.put("price", integerPrice);
                                  product.put("img_url", path);
                                  product.put("documentId", documentId);
                                  product.put("stock", integerStock);
                                  documentReference.set(product);

                                  CharSequence options[] = new CharSequence[]
                                          {
                                                  "New Products",
                                                  "Popular Products",
                                                  "Cancel"
                                          };

                                  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                  builder.setTitle("Do you want to add the products to other categories?");

                                  builder.setItems(options, new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          if (which == 0) {
                                              DocumentReference documentReference = firestore.collection("NewProducts").document(documentId);
                                              documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                      if (task.isSuccessful()) {
                                                          DocumentSnapshot document = task.getResult();
                                                          if (document.exists()) {
                                                              Toast.makeText(getActivity(), "The product already exists!", Toast.LENGTH_SHORT).show();
                                                          } else {
                                                              Map<String, Object> product = new HashMap<>();
                                                              product.put("name", productName);
                                                              product.put("type", productType);
                                                              product.put("description", productDescription);
                                                              product.put("price", integerPrice);
                                                              product.put("img_url", path);
                                                              product.put("documentId", documentId);
                                                              product.put("stock", integerStock);
                                                              documentReference.set(product);

                                                              Toast.makeText(getActivity(), "The product is added!", Toast.LENGTH_SHORT).show();
                                                              Intent intent = new Intent(getActivity().getApplication(), MainActivity.class);
                                                              startActivity(intent);
                                                          }
                                                      } else {
                                                          Toast.makeText(getActivity(), "Failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              });

                                              storageReference = FirebaseStorage.getInstance().getReference("images/*");

                                              storageReference.putFile(path)
                                                      .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                          @Override
                                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                              Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                                          }
                                                      }).addOnFailureListener(new OnFailureListener() {
                                                  @Override
                                                  public void onFailure(@NonNull Exception e) {
                                                      if (path == null) {
                                                          Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              });
                                          }

                                          if (which == 1) {
                                              DocumentReference documentReference = firestore.collection("PopularProducts").document(documentId);
                                              documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                      if (task.isSuccessful()) {
                                                          DocumentSnapshot document = task.getResult();
                                                          if (document.exists()) {
                                                              Toast.makeText(getActivity(), "The product already exists!", Toast.LENGTH_SHORT).show();
                                                          } else {
                                                              Map<String, Object> product = new HashMap<>();
                                                              product.put("name", productName);
                                                              product.put("type", productType);
                                                              product.put("description", productDescription);
                                                              product.put("price", integerPrice);
                                                              product.put("img_url", path);
                                                              product.put("documentId", documentId);
                                                              product.put("stock", integerStock);
                                                              documentReference.set(product);

                                                              Toast.makeText(getActivity(), "The product is added!", Toast.LENGTH_SHORT).show();
                                                              Intent intent = new Intent(getActivity().getApplication(), MainActivity.class);
                                                              startActivity(intent);
                                                          }
                                                      } else {
                                                          Toast.makeText(getActivity(), "Failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              });

                                              storageReference = FirebaseStorage.getInstance().getReference("images/*");

                                              storageReference.putFile(path)
                                                      .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                          @Override
                                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                              Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                                          }
                                                      }).addOnFailureListener(new OnFailureListener() {
                                                  @Override
                                                  public void onFailure(@NonNull Exception e) {
                                                      if (path == null) {
                                                          Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              });
                                          }
                                          if (which == 2) {
                                              Toast.makeText(getActivity(), "The product is added!", Toast.LENGTH_SHORT).show();
                                              Intent intent = new Intent(getActivity().getApplication(), MainActivity.class);
                                              startActivity(intent);
                                              dialog.dismiss();
                                          }
                                      }
                                  });
                                  builder.show();
                              }
                          }
                          else
                          {
                              Toast.makeText(getActivity(), "Failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                          }
                      }
                });

                storageReference = FirebaseStorage.getInstance().getReference("images/*");

                storageReference.putFile(path)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (path == null) {
                            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



                /*
                String[] categories = {"All Products", "New Products", "Popular Products"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select the Categories");
                builder.setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choices = categories[which];
                        Toast.makeText(getActivity(), "Option has been selected", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            DocumentReference documentReference = firestore.collection("ShowAll").document(documentId);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            Toast.makeText(getActivity(),"The product already exists!", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Map<String, Object> product = new HashMap<>();
                                            product.put("name", productName);
                                            product.put("type", productType);
                                            product.put("description", productDescription);
                                            product.put("price", integerPrice);
                                            product.put("rating", productRating);
                                            product.put("img_url", path);
                                            product.put("documentId", documentId);
                                            documentReference.set(product);

                                            Toast.makeText(getActivity(),"The product is added!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity().getApplication(), ShowAllActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(), "Failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            storageReference = FirebaseStorage.getInstance().getReference("images/*");

                            storageReference.putFile(path)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        if (which == 1) {
                            DocumentReference documentReference = firestore.collection("NewProducts").document(documentId);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            Toast.makeText(getActivity(),"The product already exists!", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Map<String, Object> product = new HashMap<>();
                                            product.put("name", productName);
                                            product.put("type", productType);
                                            product.put("description", productDescription);
                                            product.put("price", integerPrice);
                                            product.put("rating", productRating);
                                            product.put("img_url", path);
                                            product.put("documentId", documentId);
                                            documentReference.set(product);

                                            Toast.makeText(getActivity(),"The product is added!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity().getApplication(), ShowAllActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(), "Failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            storageReference = FirebaseStorage.getInstance().getReference("images/*");

                            storageReference.putFile(path)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        if (which == 2) {
                            DocumentReference documentReference = firestore.collection("PopularProducts").document(documentId);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            Toast.makeText(getActivity(),"The product already exists!", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Map<String, Object> product = new HashMap<>();
                                            product.put("name", productName);
                                            product.put("type", productType);
                                            product.put("description", productDescription);
                                            product.put("price", integerPrice);
                                            product.put("rating", productRating);
                                            product.put("img_url", path);
                                            product.put("documentId", documentId);
                                            documentReference.set(product);

                                            Toast.makeText(getActivity(),"The product is added!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity().getApplication(), ShowAllActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(), "Failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            storageReference = FirebaseStorage.getInstance().getReference("images/*");

                            storageReference.putFile(path)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

                 */
            }

        });

        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(intent);

            }
        });

        return view;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        path = data.getData();
                        imgProduct.setImageURI(path);
                    }
                }
            });
}