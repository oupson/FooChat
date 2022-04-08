package fr.foo.foochat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import fr.foo.foochat.databinding.DeviceItemBinding;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.DeviceViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Device model);
    }

    public final List<Device> devicesList;
    private final OnItemClickListener clickListener;

    public static class Device {
        private final String name;
        private final String macAddress;

        public Device(@Nullable String name, @NonNull String macAddress) {
            this.name = name;
            this.macAddress = macAddress;
        }

        @Nullable
        public String getName() {
            return this.name;
        }

        @NonNull
        public String getMacAddress() {
            return this.macAddress;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;

            if (other instanceof Device) {
                Device device = (Device) other;
                return Objects.equals(device.name, this.name) && device.macAddress.equals(this.macAddress);
            } else if (other instanceof String) {
                System.out.println(other);
                String deviceMac = (String) other;
                return deviceMac.equals(this.macAddress);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, macAddress);
        }
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final DeviceItemBinding binding;

        public DeviceViewHolder(@NonNull DeviceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Device device) {
            this.binding.deviceItemNameTextView.setText(device.getName());
            this.binding.deviceItemMacTextView.setText(device.getMacAddress());
            this.binding.getRoot().setOnClickListener((v) -> BluetoothDeviceAdapter.this.clickListener.onItemClick(device));
        }
    }

    public BluetoothDeviceAdapter(List<Device> devicesList, OnItemClickListener clickListener) {
        super();
        this.devicesList = devicesList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(
                DeviceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.bind(this.devicesList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.devicesList.size();
    }
}
